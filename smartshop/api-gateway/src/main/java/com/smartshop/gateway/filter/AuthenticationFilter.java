package com.smartshop.gateway.filter;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * AuthenticationFilter - Validates JWT bearer tokens at the API edge.
 *
 * <h2>Purpose</h2>
 * The gateway should reject unauthenticated requests before they consume downstream service resources. JWTs contain
 * a header, payload, and signature; validating the signature proves the token was issued by a trusted party.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>HS256: A symmetric signing algorithm where the same secret signs and verifies tokens.</li>
 *   <li>GlobalFilter: A Spring Cloud Gateway hook that sees every request before routing.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Requests enter the gateway, this filter validates protected paths, then the route filter forwards valid traffic.
 *
 * @see LoggingFilter
 * @author SmartShop Team
 */
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {
    private static final Set<String> PUBLIC_PATH_PREFIXES = Set.of("/auth/register", "/auth/login", "/actuator", "/swagger-ui", "/v3/api-docs");
    private final SecretKey key;

    /**
     * Constructor injection makes the JWT secret explicit and testable.
     *
     * @param secret shared HMAC secret used to verify user-service tokens
     */
    public AuthenticationFilter(@Value("${security.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Validates bearer tokens on protected routes and short-circuits with 401 when verification fails.
     *
     * @param exchange reactive request/response context
     * @param chain next filter in the gateway chain
     * @return a Mono that completes when filtering and routing are done
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (PUBLIC_PATH_PREFIXES.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }
        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        try {
            String token = authorization.substring("Bearer ".length());
            // Parsing the signed claims verifies the signature and expiration before the request is trusted.
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return chain.filter(exchange);
        } catch (JwtException ex) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    /**
     * Places authentication early so rejected calls never reach rate-limited or downstream resources.
     *
     * @return filter order where lower values run first
     */
    @Override
    public int getOrder() {
        return -100;
    }
}
