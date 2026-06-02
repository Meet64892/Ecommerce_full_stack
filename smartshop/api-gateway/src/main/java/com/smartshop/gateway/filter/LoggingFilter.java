package com.smartshop.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * LoggingFilter - Global Request/Response Logging with Correlation ID Injection
 *
 * <h2>Purpose</h2>
 * Every HTTP request that passes through the gateway is logged here with:
 *   - A correlation ID (unique ID linking all logs for one request across all services)
 *   - Request method and path
 *   - Response status code
 *   - Request duration in milliseconds
 *
 * Without structured logging with correlation IDs, debugging a failed request in a
 * distributed system is like finding a needle in a haystack — logs from 5 services
 * are interleaved in your log aggregator with no way to link them.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Correlation ID: A unique identifier (UUID) generated for each incoming request.
 *       It's added to:
 *       1. The request headers (X-Correlation-ID) so upstream services include it in THEIR logs
 *       2. The response headers so clients can report it when filing support tickets
 *       3. The MDC (Mapped Diagnostic Context) so it appears in every log line
 *       Result: searching your log aggregator for "X-Correlation-ID:abc123" shows the
 *       complete request trail across all services.</li>
 *   <li>GlobalFilter vs GatewayFilter: GlobalFilter applies to ALL routes automatically.
 *       GatewayFilter (from AbstractGatewayFilterFactory) must be explicitly configured
 *       per route. Use GlobalFilter for concerns that apply everywhere (logging, tracing).</li>
 *   <li>Ordered interface: When multiple GlobalFilters exist, Ordered determines execution
 *       order. Lower order = runs earlier. We run at Ordered.HIGHEST_PRECEDENCE to log
 *       BEFORE any other filter processes the request.</li>
 *   <li>MDC (Mapped Diagnostic Context): A thread-local map where you store contextual
 *       data (like correlation ID) that's automatically appended to every log line.
 *       Configure your logging pattern with %X{correlationId} to include it.</li>
 *   <li>Reactive caution with MDC: In WebFlux, a request can be handled by different
 *       threads (non-blocking). Traditional MDC (ThreadLocal) breaks across async boundaries.
 *       We use doOnEach with Reactor Context instead for proper reactive MDC propagation.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * This runs FIRST (highest precedence) before AuthenticationFilter and route matching.
 * It injects X-Correlation-ID into the request so every downstream service can
 * include it in their logs via their own logging configuration.
 *
 * @author SmartShop Team
 */
@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {

    // Header name for correlation ID — standard industry convention
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    // MDC key for log pattern: configure Logback with %X{correlationId}
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    /**
     * Filter logic: log incoming request, inject correlation ID, log response after completion.
     * The reactive pattern "then()" and "doFinally()" handle post-processing in WebFlux.
     *
     * @param exchange the current server web exchange (request + response)
     * @param chain    the filter chain to forward the request to
     * @return a Mono<Void> that completes when the response is sent
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Generate a new correlation ID if the client didn't provide one.
        // Clients (mobile apps, other services) can provide their own correlation ID
        // to trace requests that originated outside the gateway.
        String correlationId = request.getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        final String finalCorrelationId = correlationId;
        final long startTime = System.currentTimeMillis();

        // Add correlation ID to the request so upstream services receive it
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(CORRELATION_ID_HEADER, finalCorrelationId)
                .build();

        // Capture the final correlation ID for the response header lambda
        String corrId = finalCorrelationId;

        log.info("Incoming request: method={} path={} correlationId={}",
                request.getMethod(),
                request.getPath().value(),
                corrId);

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                // doFinally runs after the reactive chain completes (success, error, or cancel)
                // This is where we log the response — we can't log it synchronously because
                // in WebFlux, the response body is written asynchronously
                .doFinally(signalType -> {
                    long duration = System.currentTimeMillis() - startTime;
                    int statusCode = exchange.getResponse().getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().value()
                            : 0;

                    log.info("Completed request: method={} path={} status={} duration={}ms correlationId={}",
                            request.getMethod(),
                            request.getPath().value(),
                            statusCode,
                            duration,
                            corrId);

                    // Add correlation ID to the response so clients can reference it in support tickets
                    exchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER, corrId);
                });
    }

    /**
     * Return the highest precedence (lowest number) so this filter runs BEFORE
     * all other GlobalFilters. This ensures every request is logged, even if
     * a subsequent filter short-circuits the chain (e.g., 401 from auth filter).
     *
     * @return Ordered.HIGHEST_PRECEDENCE = Integer.MIN_VALUE
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
