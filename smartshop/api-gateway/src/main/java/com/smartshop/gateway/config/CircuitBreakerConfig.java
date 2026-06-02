package com.smartshop.gateway.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * CircuitBreakerConfig - Supplies fallback endpoints used by Gateway circuit breakers.
 *
 * <h2>Purpose</h2>
 * A circuit breaker is a state machine: CLOSED allows calls, OPEN fails fast after repeated failures, and
 * HALF_OPEN probes whether the dependency has recovered. Fallback responses keep client failures graceful.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Bulkhead thinking: A failing service should not exhaust gateway threads or client patience.</li>
 *   <li>Fallback route: Gateway forwards to a local endpoint when downstream calls fail.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Route definitions use `fallbackUri: forward:/fallback/...` and these methods produce safe 503 responses.
 *
 * @see RateLimiterConfig
 * @author SmartShop Team
 */
@RestController
public class CircuitBreakerConfig {
    /**
     * Returns a generic fallback for unavailable downstream services.
     *
     * @return HTTP 503 response with a small diagnostic body
     */
    @GetMapping("/fallback/service")
    public ResponseEntity<Map<String, String>> serviceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "Service is temporarily unavailable", "status", "degraded"));
    }
}
