package com.smartshop.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * AuthenticationFilter - Global JWT validation filter.
 *
 * <h2>Purpose</h2>
 * This filter enforces stateless authentication at the edge so downstream services receive only
 * pre-validated calls. Centralizing auth in the gateway reduces duplicate token parsing logic.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>JWT structure: header.payload.signature signed with HMAC key.</li>
 *   <li>Filter chain: each filter decorates request/response behavior before routing.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Runs before service routing; authenticated user identity is added as headers for downstream use.
 *
 * @see LoggingFilter
 * @author SmartShop Team
 */
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final List<String> PUBLIC_PATH_PREFIXES = List.of("/auth/", "/actuator/", "/swagger-ui", "/v3/api-docs");

    private final SecretKey secretKey;

    /**
     * Creates filter with configured JWT secret.
     *
     * @param jwtSecret HMAC secret used to validate token signatures
     */
    public AuthenticationFilter(@Value("${security.jwt.secret}") final String jwtSecret) {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Validates incoming JWT and enriches request headers.
     *
     * @param exchange current web exchange
     * @param chain gateway filter chain
     * @return completion signal once downstream filters finish
     */
    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        final String path = exchange.getRequest().getURI().getPath();
        if (PUBLIC_PATH_PREFIXES.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        final String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            final String token = authHeader.substring(7);
            final Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

            final ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Email", claims.getSubject())
                    .header("X-User-Role", claims.get("role", String.class))
                    .build();
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception ex) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    /**
     * Ensures auth runs early before most filters.
     *
     * @return high-precedence order
     */
    @Override
    public int getOrder() {
        return -100;
    }
}
