package com.smartshop.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * AuthenticationFilter - Global JWT validation at the edge.
 *
 * <h2>Purpose</h2>
 * Authentication is a cross-cutting concern. Rather than re-validate the JWT in
 * every microservice, the gateway validates it once at the boundary and rejects
 * unauthenticated requests early — downstream services can then trust the
 * forwarded identity headers (in a hardened setup, services still verify, but
 * the edge stops obvious bad traffic cheaply).
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Open vs protected routes</b>: login/registration and Swagger must be
 *       reachable WITHOUT a token, so we maintain an allow-list of open paths.</li>
 *   <li><b>Bearer scheme</b>: tokens arrive as {@code Authorization: Bearer
 *       <jwt>}. We strip the prefix and verify the HMAC signature with the same
 *       shared secret the user-service signs with (HS256).</li>
 *   <li><b>Identity propagation</b>: on success we add {@code X-Auth-User} /
 *       {@code X-Auth-Roles} headers so downstream services know who the caller
 *       is without re-parsing the token.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Runs after {@link LoggingFilter}; short-circuits with 401 on missing/invalid
 * tokens, otherwise enriches the request and forwards it.
 *
 * @see LoggingFilter
 * @author SmartShop Team
 */
@Slf4j
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    // Paths that must be reachable without authentication (login, register, docs).
    private static final List<String> OPEN_API_ENDPOINTS = List.of(
            "/api/users/auth/register",
            "/api/users/auth/login",
            "/swagger-ui",
            "/v3/api-docs",
            "/actuator"
    );

    /** Shared HMAC secret — MUST match user-service's jwt.secret. */
    private final SecretKey signingKey;

    /**
     * @param secret the HS256 signing secret injected from configuration
     */
    public AuthenticationFilter(@Value("${jwt.secret:smartshop-dev-secret-key-change-me-in-production-please-32bytes}") String secret) {
        // Derive an HMAC-SHA key from the configured secret bytes.
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Validates the JWT (when the route is protected) and forwards identity.
     *
     * @param exchange the current request/response exchange
     * @param chain    the remaining filter chain
     * @return a {@link Mono} that completes the pipeline, or a 401 response
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Public endpoints bypass auth entirely.
        if (isOpen(path)) {
            return chain.filter(exchange);
        }

        // Protected: an Authorization: Bearer header is mandatory.
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or malformed Authorization header");
        }

        String token = authHeader.substring(7);
        try {
            // parseSignedClaims throws if the signature is invalid or the token
            // is expired/tampered — exactly the cases we want to reject.
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Forward verified identity so downstream services don't re-parse.
            ServerHttpRequest mutated = request.mutate()
                    .header("X-Auth-User", claims.getSubject())
                    .header("X-Auth-Roles", String.valueOf(claims.get("roles")))
                    .build();
            return chain.filter(exchange.mutate().request(mutated).build());

        } catch (Exception ex) {
            log.warn("JWT validation failed for path {}: {}", path, ex.getMessage());
            return unauthorized(exchange, "Invalid or expired token");
        }
    }

    /**
     * @param path the request path
     * @return true if the path is in the public allow-list
     */
    private boolean isOpen(String path) {
        return OPEN_API_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    /**
     * Short-circuits the chain with a 401 Unauthorized.
     *
     * @param exchange the exchange whose response we complete
     * @param reason   logged reason (kept out of the body to avoid leaking info)
     * @return a completed {@link Mono} representing the terminated request
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String reason) {
        log.warn("Rejecting unauthenticated request: {}", reason);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    /**
     * @return order placing auth right after logging but before routing
     */
    @Override
    public int getOrder() {
        // Run just after LoggingFilter (HIGHEST_PRECEDENCE) so the correlation
        // id is already present, but before the request is routed downstream.
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
