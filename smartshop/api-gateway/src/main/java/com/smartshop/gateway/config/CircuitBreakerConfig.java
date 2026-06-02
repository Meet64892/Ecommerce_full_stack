package com.smartshop.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.time.Duration;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

/**
 * CircuitBreakerConfig - Resilience4j Circuit Breaker Configuration for Routes
 *
 * <h2>Purpose</h2>
 * When a downstream service (product-service, order-service) becomes slow or
 * unavailable, without a circuit breaker, the gateway would:
 *   1. Queue requests waiting for the service
 *   2. Exhaust threads/connections waiting for responses
 *   3. Cascade the failure to the gateway itself
 *   4. Make the entire platform unavailable, not just one service
 *
 * The circuit breaker prevents cascading failures by "tripping" (opening) when
 * a service fails too often, immediately returning fallback responses instead
 * of waiting for a timeout.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Circuit Breaker State Machine — three states:
 *     <pre>
 *       CLOSED  ←───────────── HALF-OPEN
 *         │                       ↑
 *         │ (failure rate too high)│ (wait duration expires)
 *         ↓                       │
 *       OPEN ────────────────────→
 *     </pre>
 *     - CLOSED: Normal operation. Requests flow through. Failures are counted.
 *     - OPEN: Circuit is "tripped". All requests return fallback immediately (no waiting).
 *     - HALF-OPEN: Tentative recovery. Let a few requests through to test if service is back.
 *       If they succeed → back to CLOSED. If they fail → back to OPEN.</li>
 *   <li>Sliding Window: Circuit breaker tracks the failure rate over a SLIDING WINDOW
 *       of N recent requests (count-based) or N seconds (time-based).
 *       failureRateThreshold=50 means: if 50% of recent requests fail → OPEN.</li>
 *   <li>Time Limiter: A separate concern — maximum time to wait for a response.
 *       If product-service takes longer than 3 seconds, treat it as a failure.</li>
 *   <li>Fallback Routes: When a circuit is open, requests are forwarded to /fallback/*
 *       endpoints defined in this class. These return cached data or graceful errors
 *       rather than timing out or showing generic error pages.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Configuration
public class CircuitBreakerConfig {

    /**
     * Customizes the default Resilience4j circuit breaker settings for ALL routes
     * that use circuit breaking via the gateway's CircuitBreaker filter.
     * Individual routes can override these defaults with named configurations.
     *
     * @return a Customizer that configures the ReactiveResilience4JCircuitBreakerFactory
     */
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id ->
                new Resilience4JConfigBuilder(id)
                        .timeLimiterConfig(
                                // Maximum time to wait for upstream service response.
                                // If product-service doesn't respond within 3 seconds → failure.
                                // Without this, slow services can hold the gateway's thread indefinitely.
                                TimeLimiterConfig.custom()
                                        .timeoutDuration(Duration.ofSeconds(3))
                                        .build()
                        )
                        .circuitBreakerConfig(
                                io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                                        // Use a COUNT-based sliding window of last 10 requests
                                        .slidingWindowType(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                                        .slidingWindowSize(10)
                                        // Require at least 5 requests before calculating failure rate
                                        // Prevents tripping on the first one or two failures at startup
                                        .minimumNumberOfCalls(5)
                                        // Open the circuit when 50% of requests fail
                                        .failureRateThreshold(50)
                                        // Wait 10 seconds in OPEN state before transitioning to HALF-OPEN
                                        .waitDurationInOpenState(Duration.ofSeconds(10))
                                        // Allow 3 test requests in HALF-OPEN state to check service health
                                        .permittedNumberOfCallsInHalfOpenState(3)
                                        // Slow calls (approaching the timeout) also count as failures
                                        .slowCallRateThreshold(80)
                                        .slowCallDurationThreshold(Duration.ofSeconds(2))
                                        .build()
                        )
                        .build()
        );
    }

    /**
     * Fallback route handlers — these endpoints are called when a circuit is open.
     * In production, these would:
     *   - Return cached data from Redis
     *   - Return a "service temporarily unavailable" message
     *   - Trigger an alert/notification
     *
     * @return a RouterFunction handling all /fallback/* paths
     */
    @Bean
    public RouterFunction<ServerResponse> fallbackRoutes() {
        return RouterFunctions
                // Fallback for product-service
                .route(GET("/fallback/products"),
                        request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .bodyValue("{\"success\":false,\"message\":\"Product service is temporarily unavailable. Please try again in a moment.\",\"timestamp\":\"" + java.time.Instant.now() + "\"}")
                )
                // Fallback for order-service
                .andRoute(GET("/fallback/orders"),
                        request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .bodyValue("{\"success\":false,\"message\":\"Order service is temporarily unavailable. Your order was not placed.\",\"timestamp\":\"" + java.time.Instant.now() + "\"}")
                );
    }
}
