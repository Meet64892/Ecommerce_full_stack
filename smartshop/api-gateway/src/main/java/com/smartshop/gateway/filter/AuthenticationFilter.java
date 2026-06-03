package com.smartshop.gateway.filter;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
 * Catalog read APIs (GET /products, GET /categories) are public so shoppers can browse without logging in.
 */
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {
    private static final Set<String> PUBLIC_PATH_PREFIXES = Set.of(
            "/auth/register",
            "/auth/login",
            "/actuator",
            "/swagger-ui",
            "/v3/api-docs",
            "/fallback");

    private final SecretKey key;

    public AuthenticationFilter(@Value("${security.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        if (isPublicPath(path, method)) {
            return chain.filter(exchange);
        }

        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        try {
            String token = authorization.substring("Bearer ".length());
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return chain.filter(exchange);
        } catch (JwtException ex) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isPublicPath(String path, HttpMethod method) {
        if (PUBLIC_PATH_PREFIXES.stream().anyMatch(path::startsWith)) {
            return true;
        }
        if (method == null) {
            return false;
        }
        if (method == HttpMethod.GET || method == HttpMethod.OPTIONS) {
            return path.startsWith("/products") || path.startsWith("/categories");
        }
        return false;
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
