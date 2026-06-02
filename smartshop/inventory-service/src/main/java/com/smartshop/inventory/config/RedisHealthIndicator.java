package com.smartshop.inventory.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * RedisHealthIndicator - Custom actuator health indicator for Redis.
 *
 * <h2>Purpose</h2>
 * Extends health reporting with explicit Redis connectivity checks used by readiness probes.
 * Liveness probes indicate process is alive, while readiness indicates dependency availability.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Readiness probe: should fail when critical dependency is unavailable.</li>
 *   <li>Liveness probe: should fail only when app is unrecoverably broken.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Included in /actuator/health response for Kubernetes-like operational checks.
 *
 * @see org.springframework.boot.actuate.health.HealthIndicator
 * @author SmartShop Team
 */
@Component
public class RedisHealthIndicator implements HealthIndicator {

    private final StringRedisTemplate redisTemplate;

    /**
     * Creates indicator with redis template dependency.
     *
     * @param redisTemplate redis operations template
     */
    public RedisHealthIndicator(final StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Checks Redis connectivity using ping command.
     *
     * @return UP when ping succeeds, DOWN otherwise
     */
    @Override
    public Health health() {
        try {
            final String pong = redisTemplate.getConnectionFactory().getConnection().ping();
            return Health.up().withDetail("redis.ping", pong).build();
        } catch (Exception ex) {
            return Health.down(ex).build();
        }
    }
}
