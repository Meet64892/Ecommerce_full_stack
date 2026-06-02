package com.smartshop.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * AuthenticationFilter - JWT Token Validation Gateway Filter
 *
 * <h2>Purpose</h2>
 * This filter intercepts every request that requires authentication and validates
 * the JWT token in the Authorization header. By doing this at the gateway level,
 * ALL microservices behind the gateway are protected without each service needing
 * to implement JWT validation logic themselves. This is the "single responsibility"
 * benefit of having an API gateway.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>JWT Structure: A JWT has three base64url-encoded parts separated by dots:
 *       HEADER.PAYLOAD.SIGNATURE
 *       - Header: algorithm (HS256) and token type (JWT)
 *       - Payload (Claims): userId, email, roles, expiration time
 *       - Signature: HMAC-SHA256(base64(header) + "." + base64(payload), secretKey)
 *       The signature ensures the token wasn't tampered with. If you change even
 *       one character of the payload, the signature won't match and validation fails.</li>
 *   <li>HS256 vs RS256: HS256 uses a SHARED secret (symmetric) — both issuer and
 *       validator need the same key. RS256 uses an asymmetric key pair — issuer
 *       signs with private key, validators verify with public key. RS256 is better
 *       for microservices because you don't need to share the private key.</li>
 *   <li>Stateless Authentication: JWTs carry all necessary claims in the token itself.
 *       The gateway doesn't need to call user-service for every request to check if
 *       the user is valid. The signature guarantees authenticity.</li>
 *   <li>GatewayFilterFactory: Spring Cloud Gateway uses a factory pattern for filters.
 *       AbstractGatewayFilterFactory allows configuration via YAML
 *       (filters: - AuthenticationFilter) and via Java routes.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Applied to all routes EXCEPT /auth/register and /auth/login (which don't require auth).
 * After validation, injects userId and roles as request headers so upstream services
 * can read them without re-validating the token.
 *
 * @author SmartShop Team
 */
@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    // JWT secret must match the secret used by user-service to sign tokens.
    // Both services read the same secret from config-server.
    @Value("${jwt.secret:SmartShopJwtSecretKey2024VeryLongAndSecureKeyForHS256Algorithm}")
    private String jwtSecret;

    // These paths bypass authentication — you need to reach them without a token
    // to GET a token in the first place (chicken-and-egg problem)
    private static final List<String> OPEN_ENDPOINTS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/actuator",
            "/swagger-ui",
            "/v3/api-docs"
    );

    public AuthenticationFilter() {
        super(Config.class);
    }

    /**
     * Core filter logic: extract, parse, and validate the JWT token.
     * Returns 401 Unauthorized if the token is missing, malformed, or expired.
     * Forwards the request with injected user claims if the token is valid.
     *
     * @param config the filter configuration (unused here but required by the factory pattern)
     * @return a GatewayFilter that validates JWT tokens
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().value();

            // Skip authentication for open endpoints (login, register, docs)
            // isOpenEndpoint() checks if the request path starts with any open path
            if (isOpenEndpoint(path)) {
                log.debug("Skipping authentication for open endpoint: {}", path);
                return chain.filter(exchange);
            }

            // Extract Authorization header
            // Standard format: "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Missing or malformed Authorization header for path: {}", path);
                return unauthorizedResponse(exchange, "Missing Authorization header");
            }

            // Extract the token by removing the "Bearer " prefix (7 characters)
            String token = authHeader.substring(7);

            try {
                // Parse and validate the JWT token
                // This validates: signature (wasn't tampered), expiration (not expired)
                Claims claims = parseToken(token);

                // Extract user information from claims
                String userId = claims.getSubject();
                String roles = claims.get("roles", String.class);

                log.debug("Authenticated request from userId={} to path={}", userId, path);

                // Inject user information as headers for downstream services.
                // This allows user-service, order-service, etc. to know WHO is making
                // the request without re-parsing the JWT token.
                // These headers are added by the GATEWAY — never by the client —
                // which prevents clients from forging them.
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Roles", roles != null ? roles : "")
                        .header("X-Auth-Token", token)
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (JwtException e) {
                // JwtException covers: ExpiredJwtException, SignatureException, MalformedJwtException
                log.warn("JWT validation failed for path {}: {}", path, e.getMessage());
                return unauthorizedResponse(exchange, "Invalid or expired token");
            }
        };
    }

    /**
     * Parses and validates a JWT token string.
     * The Jwts.parser() method performs BOTH parsing AND validation in one call:
     *   - Checks the signature against the secret key
     *   - Verifies the expiration (exp claim)
     *   - Verifies the "not before" time (nbf claim) if present
     *
     * @param token the raw JWT string (without "Bearer " prefix)
     * @return the validated claims from the token payload
     * @throws JwtException if the token is invalid, expired, or tampered with
     */
    private Claims parseToken(String token) {
        // Build the signing key from the secret string
        // The secret must be at least 256 bits (32 characters) for HS256
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.parser()
                .verifyWith(key)           // Set the key used for signature verification
                .build()
                .parseSignedClaims(token)  // Parse + verify (throws JwtException if invalid)
                .getPayload();             // Extract the claims from the verified token
    }

    /**
     * Creates a 401 Unauthorized response and completes the reactive chain.
     * In WebFlux, response writing is asynchronous — we return a Mono<Void>
     * that completes when the response has been fully written.
     *
     * @param exchange the current server web exchange
     * @param message  the reason for the 401 (for logging; not sent to client for security)
     * @return a Mono that completes after writing the 401 response
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        // Add WWW-Authenticate header as per RFC 7235 — tells the client what
        // authentication scheme is expected
        response.getHeaders().add(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"SmartShop\"");
        log.debug("Returning 401: {}", message);
        // setComplete() signals the response is done without writing a body
        return response.setComplete();
    }

    /**
     * Checks if the request path matches any of the open (unauthenticated) endpoints.
     *
     * @param path the request path to check
     * @return true if authentication should be skipped for this path
     */
    private boolean isOpenEndpoint(String path) {
        return OPEN_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    /**
     * Configuration class for this filter factory.
     * Currently has no configuration properties — the filter is stateless.
     * Adding configuration here (e.g., which roles are required) would allow
     * different routes to have different authentication requirements.
     */
    public static class Config {
        // Future: add role requirements, custom header names, etc.
    }
}
