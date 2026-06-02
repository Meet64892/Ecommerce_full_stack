package com.smartshop.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CircuitBreakerConfig - Resilience4j circuit breaker setup.
 *
 * <h2>Purpose</h2>
 * Circuit breakers prevent cascading failures by short-circuiting calls to unhealthy services.
 * The state machine transitions CLOSED -> OPEN -> HALF_OPEN based on observed failures.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Failure threshold: percentage of failures that trips the breaker OPEN.</li>
 *   <li>Half-open probing: limited trial calls test whether downstream has recovered.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Gateway routes use this registry to apply named circuit breaker policies per downstream service.
 *
 * @see io.github.resilience4j.circuitbreaker.CircuitBreaker
 * @author SmartShop Team
 */
@Configuration
public class CircuitBreakerConfig {

    /**
     * Provides a reusable circuit breaker registry.
     *
     * @return configured registry bean
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        final io.github.resilience4j.circuitbreaker.CircuitBreakerConfig config = io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .slidingWindowSize(20)
                .permittedNumberOfCallsInHalfOpenState(5)
                .build();
        return CircuitBreakerRegistry.of(config);
    }
}
