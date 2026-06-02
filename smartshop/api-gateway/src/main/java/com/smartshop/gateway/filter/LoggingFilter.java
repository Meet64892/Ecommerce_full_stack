package com.smartshop.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * LoggingFilter - Adds and logs a correlation id for each gateway request.
 *
 * <h2>Purpose</h2>
 * A correlation id gives support engineers one identifier to follow across gateway logs, service logs, and Kafka
 * events. Distributed tracing also propagates trace ids through B3 headers, and this filter complements that with
 * a business-friendly `X-Correlation-Id` header.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>MDC: Mapped Diagnostic Context enriches log lines without passing values through every method.</li>
 *   <li>Log levels: info records request flow while debug/trace can be enabled for deeper diagnostics.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * The filter runs for every request, adds the header, and downstream services can preserve it in logs and events.
 *
 * @see AuthenticationFilter
 * @author SmartShop Team
 */
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    /**
     * Logs request start and completion while preserving or creating a correlation id.
     *
     * @param exchange reactive request/response context
     * @param chain next gateway filter
     * @return a Mono that completes after the downstream response is written
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        String finalCorrelationId = correlationId;
        ServerWebExchange mutated = exchange.mutate()
                .request(builder -> builder.header(CORRELATION_ID_HEADER, finalCorrelationId))
                .build();
        MDC.put("correlationId", finalCorrelationId);
        log.info("Gateway received {} {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI());
        return chain.filter(mutated)
                .doFinally(signalType -> {
                    log.info("Gateway completed {} with status {}", finalCorrelationId, mutated.getResponse().getStatusCode());
                    MDC.remove("correlationId");
                });
    }

    /**
     * Runs after authentication so unauthorized requests are still logged with a correlation id.
     *
     * @return filter order
     */
    @Override
    public int getOrder() {
        return -90;
    }
}
