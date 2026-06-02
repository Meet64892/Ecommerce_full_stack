package com.smartshop.gateway.controller;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * FallbackController - Endpoint used by circuit breaker fallback routes.
 *
 * <h2>Purpose</h2>
 * Explicit fallback responses provide deterministic client behavior when downstream services are
 * unavailable. This avoids long hanging requests and clarifies resilience posture.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Graceful degradation: return controlled failures quickly.</li>
 *   <li>Fail-fast behavior: protects thread pools and event loops.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Route-level circuit breakers forward to this endpoint when OPEN or when calls time out.
 *
 * @see com.smartshop.gateway.config.CircuitBreakerConfig
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    /**
     * Returns generic fallback error payload.
     *
     * @return map describing degraded gateway response
     * @throws ResponseStatusException always thrown as service unavailable
     */
    @GetMapping
    public Map<String, Object> fallback() {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Downstream service temporarily unavailable at " + Instant.now());
    }
}
