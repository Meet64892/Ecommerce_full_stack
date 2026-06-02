package com.smartshop.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * RateLimiterConfig - Defines how requests are counted for Redis rate limiting.
 *
 * <h2>Purpose</h2>
 * Rate limiting protects the platform from abuse: brute-force login attempts,
 * scraping, accidental client retry storms, and outright DDoS. It also enforces
 * <i>fair usage</i> so one noisy client cannot starve everyone else. Spring
 * Cloud Gateway's {@code RequestRateLimiter} filter implements a token-bucket
 * algorithm whose counters live in Redis (so the limit is shared across all
 * gateway instances, not per-JVM).
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>KeyResolver</b>: decides the bucket key — i.e. "who" we are limiting.
 *       Here we key by authenticated user (falling back to client IP) so each
 *       principal gets its own quota.</li>
 *   <li><b>Token bucket</b>: tokens refill at {@code replenishRate}/sec up to
 *       {@code burstCapacity}. Each request spends a token; empty bucket -> 429.
 *       (Rate/burst are configured on the filter in application.yml.)</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * The YAML route filters reference this {@code userKeyResolver} bean to bucket
 * counters per user; Redis stores the counters.
 *
 * @author SmartShop Team
 */
@Configuration
public class RateLimiterConfig {

    /**
     * Resolves the rate-limit bucket key from the request.
     *
     * @return a {@link KeyResolver} keying on authenticated user, else client IP
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // Prefer the verified user header set by AuthenticationFilter so the
            // quota follows the principal across IP changes (mobile networks).
            String user = exchange.getRequest().getHeaders().getFirst("X-Auth-User");
            if (user != null && !user.isBlank()) {
                return Mono.just(user);
            }
            // Anonymous/public endpoints: fall back to remote IP as the key.
            String ip = exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
            return Mono.just(ip);
        };
    }
}
