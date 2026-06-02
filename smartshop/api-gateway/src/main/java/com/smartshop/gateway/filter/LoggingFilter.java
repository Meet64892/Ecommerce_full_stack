package com.smartshop.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * LoggingFilter - Global filter that logs every request and injects a correlation id.
 *
 * <h2>Purpose</h2>
 * In a distributed system a single user action fans out across services. To
 * follow that action end-to-end we attach a <b>correlation id</b> at the edge
 * and propagate it downstream as a header. Every service logs this id, so we can
 * later {@code grep} one id and reconstruct the full journey.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>GlobalFilter</b>: applies to ALL routes (unlike a per-route
 *       GatewayFilter). Ideal for universal concerns like logging.</li>
 *   <li><b>Ordered</b>: filters run in an ordered chain. A very low order value
 *       means this runs early, so the correlation id exists before other filters
 *       (and downstream services) need it.</li>
 *   <li><b>Correlation id vs trace id</b>: trace ids (B3) are managed by the
 *       tracing library for spans; this explicit {@code X-Correlation-Id} is a
 *       business-friendly handle clients can also send/log.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Runs on every inbound request, mutates it to carry {@code X-Correlation-Id},
 * then passes control down the filter chain.
 *
 * @author SmartShop Team
 */
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    /** Header name used to carry the correlation id to downstream services. */
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    /**
     * Logs the request and guarantees a correlation id is present, then forwards.
     *
     * @param exchange the current server exchange (request/response pair)
     * @param chain    the remaining filter chain to delegate to
     * @return a {@link Mono} that completes when the downstream pipeline finishes
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Reuse an incoming correlation id if the client already sent one
        // (preserves continuity across multiple gateway hops); otherwise mint one.
        String correlationId = request.getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        // Rewrite the request so the (immutable) headers carry the id downstream.
        ServerHttpRequest mutated = request.mutate()
                .header(CORRELATION_ID_HEADER, correlationId)
                .build();

        log.info("Incoming request [{}] {} {} -> correlationId={}",
                request.getMethod(), request.getURI().getPath(),
                request.getRemoteAddress(), correlationId);

        // Continue the chain with the mutated request; log again on completion.
        String finalId = correlationId;
        return chain.filter(exchange.mutate().request(mutated).build())
                .doFinally(signal -> log.info("Completed request correlationId={} status={}",
                        finalId, exchange.getResponse().getStatusCode()));
    }

    /**
     * @return a very low order so this filter runs first in the chain
     */
    @Override
    public int getOrder() {
        // Integer.MIN_VALUE = earliest possible execution.
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
