package com.smartshop.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * RateLimiterConfig - Redis-backed key resolution for gateway throttling.
 *
 * <h2>Purpose</h2>
 * Rate limiting protects APIs from abuse, accidental client storms, and denial-of-service traffic
 * while maintaining fair usage across consumers.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Token bucket: allows bursts but enforces refill-based sustained rate.</li>
 *   <li>Key resolver: identifies unique caller for per-client quotas.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * The key resolver is referenced by RequestRateLimiter filters in application.yml routes.
 *
 * @see org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter
 * @author SmartShop Team
 */
@Configuration
public class RateLimiterConfig {

    /**
     * Resolves rate-limit identity using authenticated email fallback to remote address.
     *
     * @return reactive key resolver bean
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            final String userEmail = exchange.getRequest().getHeaders().getFirst("X-User-Email");
            if (userEmail != null && !userEmail.isBlank()) {
                return Mono.just(userEmail);
            }
            final var remoteAddress = exchange.getRequest().getRemoteAddress();
            return Mono.just(remoteAddress == null ? "anonymous" : remoteAddress.getAddress().getHostAddress());
        };
    }
}
