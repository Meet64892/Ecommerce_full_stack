package com.smartshop.gateway.config;

import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

import java.time.Duration;

/**
 * CircuitBreakerConfig - Default Resilience4j circuit-breaker tuning for the gateway.
 *
 * <h2>Purpose</h2>
 * When a downstream service becomes slow or unavailable, naive retrying piles up
 * threads/connections and can cascade the failure upstream (a "retry storm").
 * A <b>circuit breaker</b> detects sustained failures and "opens" to fail fast,
 * giving the struggling service time to recover and letting the gateway return a
 * graceful fallback instead of hanging.
 *
 * <h2>Key Concepts (the breaker state machine)</h2>
 * <ul>
 *   <li><b>CLOSED</b>: normal — calls pass through; failures are counted in a
 *       sliding window.</li>
 *   <li><b>OPEN</b>: failure rate crossed the threshold; calls are short-
 *       circuited to the fallback for a wait duration (no downstream calls).</li>
 *   <li><b>HALF_OPEN</b>: after the wait, a few trial calls are permitted; if
 *       they succeed the breaker closes, otherwise it re-opens.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * The YAML routes attach a {@code CircuitBreaker} filter (named per service)
 * with a {@code fallbackUri}. This bean supplies the default thresholds and the
 * time limiter that bounds how long a single call may take.
 *
 * @author SmartShop Team
 */
@Configuration
public class CircuitBreakerConfig {

    /**
     * Customizes the default circuit breaker used by gateway routes.
     *
     * @return a {@link Customizer} applying our window/threshold/timeout defaults
     */
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                        // Use a COUNT-based window: evaluate the last N calls.
                        .slidingWindowType(SlidingWindowType.COUNT_BASED)
                        .slidingWindowSize(10)
                        // Trip to OPEN when >=50% of those 10 calls fail.
                        .failureRateThreshold(50.0f)
                        // Stay OPEN this long before allowing trial calls.
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        // Number of trial calls permitted in HALF_OPEN.
                        .permittedNumberOfCallsInHalfOpenState(3)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        // A single downstream call may not exceed 3s; otherwise it
                        // counts as a failure and feeds the breaker.
                        .timeoutDuration(Duration.ofSeconds(3))
                        .build())
                .build());
    }
}
