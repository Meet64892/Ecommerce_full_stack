package com.smartshop.gateway.controller;

import com.smartshop.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * FallbackController - Graceful responses when a circuit breaker is OPEN.
 *
 * <h2>Purpose</h2>
 * When a route's circuit breaker trips, Spring Cloud Gateway forwards the
 * request to this controller's {@code fallbackUri} instead of the dead
 * downstream service. Returning a clean, structured 503 here (rather than a
 * stack trace or a hung connection) keeps the client experience predictable.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Reactive return type ({@code Mono}) because the gateway runs on WebFlux.</li>
 *   <li>HTTP 503 (Service Unavailable) is the semantically correct status for a
 *       temporarily-down dependency.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Referenced by the {@code CircuitBreaker} filter's {@code fallbackUri:
 * forward:/fallback/{service}} in application.yml.
 *
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    /**
     * Returns a uniform "service unavailable" envelope for the tripped service.
     *
     * @param service the downstream service name captured from the path
     * @return a 503 response wrapped in the shared {@link ApiResponse} shape
     */
    @GetMapping("/{service}")
    public Mono<ResponseEntity<ApiResponse<Void>>> fallback(@PathVariable String service) {
        ApiResponse<Void> body = ApiResponse.of(
                null,
                service + " is temporarily unavailable. Please retry shortly.");
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body));
    }
}
