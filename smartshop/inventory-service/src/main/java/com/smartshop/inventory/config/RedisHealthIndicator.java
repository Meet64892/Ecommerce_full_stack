package com.smartshop.inventory.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * RedisHealthIndicator - Custom actuator health check for Redis connectivity.
 *
 * <h2>Purpose</h2>
 * Kubernetes distinguishes liveness (process should be restarted) from readiness (process can receive traffic). Redis
 * being unavailable may make the service not ready even though the JVM is alive.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Liveness probe: Answers whether the application process is alive.</li>
 *   <li>Readiness probe: Answers whether dependencies are healthy enough for traffic.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Actuator includes this indicator in `/actuator/health` so operators can see Redis dependency state.
 *
 * @see HealthIndicator
 * @author SmartShop Team
 */
@Component
public class RedisHealthIndicator implements HealthIndicator {
    private final RedisConnectionFactory connectionFactory;

    /**
     * Creates the indicator with Redis connection access.
     *
     * @param connectionFactory Redis connection factory
     */
    public RedisHealthIndicator(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**
     * Pings Redis and returns UP or DOWN based on connectivity.
     *
     * @return actuator health result
     */
    @Override
    public Health health() {
        try (RedisConnection connection = connectionFactory.getConnection()) {
            String pong = connection.ping();
            return Health.up().withDetail("redis", pong).build();
        } catch (RuntimeException ex) {
            return Health.down(ex).withDetail("redis", "unreachable").build();
        }
    }
}
