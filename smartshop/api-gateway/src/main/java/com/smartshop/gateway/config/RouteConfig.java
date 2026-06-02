package com.smartshop.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RouteConfig - Programmatic gateway route definitions.
 *
 * <h2>Purpose</h2>
 * Spring Cloud Gateway supports declarative YAML and Java DSL routing. Keeping both examples in
 * this project demonstrates how teams can choose readability (YAML) or dynamic logic (Java DSL).
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Predicates: decide when a route should match a request.</li>
 *   <li>Filters: transform request/response along the route pipeline.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Complements application.yml routes with a Java-based fallback route example.
 *
 * @see org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
 * @author SmartShop Team
 */
@Configuration
public class RouteConfig {

    /**
     * Declares Java-based routes for educational parity with YAML routes.
     *
     * @param builder route builder
     * @return built route locator
     */
    @Bean
    public RouteLocator customRouteLocator(final RouteLocatorBuilder builder) {
        return builder.routes()
                .route("health-route", route -> route
                        .path("/gateway/health")
                        .filters(filter -> filter.setPath("/actuator/health"))
                        .uri("lb://api-gateway"))
                .build();
    }
}
