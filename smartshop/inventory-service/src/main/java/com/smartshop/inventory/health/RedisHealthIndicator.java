package com.smartshop.inventory.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * RedisHealthIndicator - Custom actuator health check for Redis connectivity.
 *
 * <h2>Purpose</h2>
 * Inventory reads depend on Redis. This indicator pings Redis so the service's
 * {@code /actuator/health} (and the readiness probe) reflects cache availability.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>HealthIndicator</b>: each bean implementing it contributes a component
 *       to the aggregate health endpoint.</li>
 *   <li><b>Liveness vs readiness (Kubernetes)</b>: a <i>liveness</i> probe asks
 *       "is the process alive?" — failing it restarts the pod. A <i>readiness</i>
 *       probe asks "can it serve traffic right now?" — failing it removes the pod
 *       from the load balancer without killing it. A Redis outage should affect
 *       readiness (stop sending traffic) but not necessarily liveness (don't
 *       kill an otherwise-healthy JVM).</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Auto-registered by its component name ("redis") under the health endpoint.
 *
 * @author SmartShop Team
 */
@Component("redis")
@RequiredArgsConstructor
public class RedisHealthIndicator implements HealthIndicator {

    private final RedisConnectionFactory connectionFactory;

    /**
     * Pings Redis and reports UP/DOWN accordingly.
     *
     * @return {@link Health#up()} with the PONG reply, or {@link Health#down()}
     *         with the error if Redis is unreachable
     */
    @Override
    public Health health() {
        try {
            // A PING round-trip is the cheapest way to verify a live connection.
            String pong = connectionFactory.getConnection().ping();
            return Health.up().withDetail("ping", pong).build();
        } catch (Exception e) {
            // Surface the failure detail so operators can diagnose quickly.
            return Health.down(e).withDetail("error", e.getMessage()).build();
        }
    }
}
