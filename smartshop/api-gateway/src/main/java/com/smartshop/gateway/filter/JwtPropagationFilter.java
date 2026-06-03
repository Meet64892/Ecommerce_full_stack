package com.smartshop.gateway.filter;

import com.smartshop.common.security.MarketplaceHeaders;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
public class JwtPropagationFilter implements GlobalFilter, Ordered {
    private static final Set<String> PUBLIC_PATH_PREFIXES = Set.of(
            "/auth/register", "/auth/login", "/actuator", "/swagger-ui", "/v3/api-docs");

    private final SecretKey key;

    public JwtPropagationFilter(@Value("${security.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (PUBLIC_PATH_PREFIXES.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }
        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }
        try {
            String token = authorization.substring("Bearer ".length());
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            ServerHttpRequest.Builder builder = exchange.getRequest().mutate()
                    .header(MarketplaceHeaders.USER_EMAIL, claims.getSubject());
            Object role = claims.get("role");
            if (role != null) {
                builder.header(MarketplaceHeaders.USER_ROLE, role.toString());
            }
            Object userId = claims.get("userId");
            if (userId != null) {
                builder.header(MarketplaceHeaders.USER_ID, userId.toString());
            }
            Object brandId = claims.get("brandId");
            if (brandId != null) {
                builder.header(MarketplaceHeaders.BRAND_ID, brandId.toString());
            }
            return chain.filter(exchange.mutate().request(builder.build()).build());
        } catch (JwtException ex) {
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -90;
    }
}
