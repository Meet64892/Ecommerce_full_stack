package com.smartshop.order.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * ResilienceConfig - Defines retry and circuit-breaker defaults for order workflows.
 *
 * <h2>Purpose</h2>
 * Distributed systems fail partially, so callers should retry transient failures and stop hammering unhealthy
 * dependencies. Resilience4j provides lightweight decorators for retries and circuit breakers.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Retry: Re-attempts transient work with wait durations and max attempts.</li>
 *   <li>Circuit breaker: Opens after failures and later moves HALF_OPEN to test recovery.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * These beans document the resilience policy that can be applied to outbound calls or Kafka publishing adapters.
 *
 * @see RetryConfig
 * @author SmartShop Team
 */
@Configuration
public class ResilienceConfig {
    /**
     * Defines retry behavior with bounded attempts.
     *
     * @return retry configuration
     */
    @Bean
    public RetryConfig orderRetryConfig() {
        return RetryConfig.custom().maxAttempts(3).waitDuration(Duration.ofMillis(250)).build();
    }

    /**
     * Defines circuit-breaker behavior for order dependencies.
     *
     * @return circuit-breaker configuration
     */
    @Bean
    public CircuitBreakerConfig orderCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom().failureRateThreshold(50).waitDurationInOpenState(Duration.ofSeconds(10)).slidingWindowSize(10).build();
    }
}
