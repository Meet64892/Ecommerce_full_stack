package com.smartshop.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * RateLimiterConfig - Redis-Backed Request Rate Limiting Configuration
 *
 * <h2>Purpose</h2>
 * Rate limiting prevents a single client from overwhelming our services with requests.
 * Without rate limiting:
 *   - A buggy mobile app could accidentally DDoS our platform
 *   - A malicious actor could enumerate user IDs or brute-force passwords
 *   - A single heavy user could degrade service for everyone
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Token Bucket Algorithm: Spring Cloud Gateway's rate limiter uses the
 *       Token Bucket algorithm:
 *       - Imagine a bucket that holds tokens (requests)
 *       - Tokens are added at a constant rate (replenishRate per second)
 *       - Each request consumes one token
 *       - If the bucket is empty, the request is rejected (HTTP 429 Too Many Requests)
 *       - burstCapacity = maximum tokens the bucket can hold (allows short bursts)
 *       Example: replenishRate=10, burstCapacity=20 means:
 *         - Normally allows 10 requests/second
 *         - A burst of 20 requests at once is OK
 *         - But 21 requests at once → the 21st gets 429</li>
 *   <li>WHY Redis? Rate limit counters must be shared across all gateway instances.
 *       If you have 3 gateway pods and use in-memory counters, a client could make
 *       10 requests to pod 1, 10 to pod 2, and 10 to pod 3 — total 30 requests
 *       despite a limit of 10. Redis provides an atomic increment across all instances.</li>
 *   <li>KeyResolver: Determines HOW to identify a client for rate limiting.
 *       Options: by IP address, by user ID (from JWT), by API key.
 *       We provide beans for multiple strategies — choose in application.yml:
 *       filters: - name: RequestRateLimiter
 *                  args: key-resolver: "#{@userKeyResolver}"</li>
 *   <li>DDoS vs Rate Limiting: Rate limiting is NOT DDoS protection (DDoS requires
 *       network-level protection). It's "fair use" enforcement to prevent accidental
 *       abuse and protect business-critical endpoints.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Configuration
public class RateLimiterConfig {

    /**
     * Creates a RedisRateLimiter with default settings.
     * These defaults can be overridden per-route in application.yml.
     *
     * @param replenishRate    tokens added per second (sustained request rate)
     * @param burstCapacity    max tokens (allows temporary burst above sustained rate)
     * @param requestedTokens  tokens consumed per request (usually 1)
     * @return configured RedisRateLimiter
     */
    @Bean
    public RedisRateLimiter defaultRateLimiter() {
        // 10 requests/second sustained, 20 requests burst, 1 token per request
        return new RedisRateLimiter(10, 20, 1);
    }

    /**
     * Rate limit by authenticated user ID (from JWT, injected by AuthenticationFilter).
     * This is the preferred approach for authenticated APIs because:
     *   - Each USER gets their own rate limit bucket
     *   - Multiple users behind the same NAT/proxy don't share a limit
     *   - Unauthenticated requests fall through to IP-based limiting
     *
     * @return a KeyResolver that extracts userId from the X-User-Id header
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            if (userId != null && !userId.isBlank()) {
                // Authenticated user: limit per userId
                return Mono.just("user:" + userId);
            }
            // Unauthenticated user: fall back to IP-based limiting
            String ip = exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
            return Mono.just("ip:" + ip);
        };
    }

    /**
     * Rate limit by IP address.
     * Used for public endpoints (login, register) where we don't have a user ID yet.
     * Protects against brute-force attacks on the login endpoint.
     *
     * @return a KeyResolver that extracts the client IP address
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            // Check X-Forwarded-For first — if behind a load balancer, this contains
            // the real client IP, while getRemoteAddress() returns the load balancer IP
            String forwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
            if (forwardedFor != null && !forwardedFor.isBlank()) {
                // X-Forwarded-For can contain a chain: "client, proxy1, proxy2"
                // The first IP in the list is the original client
                return Mono.just("ip:" + forwardedFor.split(",")[0].trim());
            }
            String ip = exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
            return Mono.just("ip:" + ip);
        };
    }
}
