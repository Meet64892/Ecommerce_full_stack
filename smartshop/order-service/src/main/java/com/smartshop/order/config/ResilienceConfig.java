package com.smartshop.order.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.IntervalFunction;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ResilienceConfig - Resilience4j retry and circuit breaker config.
 *
 * <h2>Purpose</h2>
 * Protects order workflows from transient downstream failures using controlled retries and
 * fail-fast circuit breakers.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Exponential backoff retries: avoids immediate retry storms.</li>
 *   <li>Circuit breaker states: CLOSED -> OPEN -> HALF_OPEN.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Reusable resilience policies for external integration calls in saga steps.
 *
 * @see io.github.resilience4j.retry.Retry
 * @author SmartShop Team
 */
@Configuration
public class ResilienceConfig {

    /**
     * Retry registry with exponential backoff policy.
     *
     * @return retry registry
     */
    @Bean
    public RetryRegistry retryRegistry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .intervalFunction(IntervalFunction.ofExponentialBackoff(500L, 2.0d))
                .build();
        return RetryRegistry.of(config);
    }

    /**
     * Circuit breaker registry for downstream integration protection.
     *
     * @return circuit breaker registry
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                .waitDurationInOpenState(Duration.ofSeconds(5))
                .build();
        return CircuitBreakerRegistry.of(config);
    }
}
