package com.smartshop.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * RateLimiterConfig - Provides keys for Redis-backed gateway rate limiting.
 *
 * <h2>Purpose</h2>
 * Rate limiting protects downstream services from abusive clients, accidental request storms, and some DDoS
 * patterns. Redis is used because multiple gateway instances can share counters with low latency.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Fair usage: Limits are applied per logical client rather than globally.</li>
 *   <li>RequestRateLimiter: A Gateway filter that stores token-bucket state in Redis.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * YAML route filters reference the `userKeyResolver` bean to decide which Redis bucket a request consumes.
 *
 * @see CircuitBreakerConfig
 * @author SmartShop Team
 */
@Configuration
public class RateLimiterConfig {
    /**
     * Resolves a rate-limit key from a user header or remote address.
     *
     * @return reactive key resolver used by Spring Cloud Gateway
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            String key = (userId == null || userId.isBlank())
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : userId;
            return Mono.just(key);
        };
    }
}
