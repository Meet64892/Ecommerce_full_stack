package com.smartshop.gateway.filter;

import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * LoggingFilter - Correlation-aware request logging filter.
 *
 * <h2>Purpose</h2>
 * Correlation IDs make distributed logs searchable across services by attaching a stable request
 * identifier to each hop. This is foundational for troubleshooting microservice call chains.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Correlation ID: request-scoped identifier propagated via HTTP headers.</li>
 *   <li>MDC: mapped diagnostic context enriches log lines with trace metadata.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * This filter runs at the gateway edge, ensures header presence, then forwards it downstream.
 *
 * @see AuthenticationFilter
 * @author SmartShop Team
 */
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    /**
     * Adds correlation ID and logs request metadata.
     *
     * @param exchange current exchange
     * @param chain remaining filter chain
     * @return reactive completion signal
     */
    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders().getFirst("X-Correlation-Id");
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put("correlationId", correlationId);

        final ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-Correlation-Id", correlationId)
                .build();

        return chain.filter(exchange.mutate().request(request).build())
                .doFinally(signalType -> MDC.remove("correlationId"));
    }

    /**
     * Runs before authentication to ensure all logs include correlation IDs.
     *
     * @return precedence order value
     */
    @Override
    public int getOrder() {
        return -200;
    }
}
