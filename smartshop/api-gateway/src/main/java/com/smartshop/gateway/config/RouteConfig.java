package com.smartshop.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RouteConfig - Programmatic (Java DSL) route definitions.
 *
 * <h2>Purpose</h2>
 * Routes can be declared in {@code application.yml} (declarative) OR in Java
 * (programmatic). This class demonstrates the Java approach so the project shows
 * BOTH styles. The Java DSL is useful when routes need conditional logic, loops,
 * or values computed at runtime that YAML cannot express.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Predicate</b>: the matching condition (here, a path prefix). Think of
 *       it as the {@code if} of the route.</li>
 *   <li><b>Filter</b>: a transformation applied to matching requests/responses
 *       (here, stripping the {@code /api} prefix before forwarding). Think of
 *       the filter chain as a pipeline each request flows through.</li>
 *   <li><b>uri("lb://...")</b>: {@code lb} = load-balanced. The gateway resolves
 *       the service id through Eureka and picks a healthy instance.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Spring merges these programmatic routes with the YAML routes; both feed the
 * same routing engine. We keep one route (product) here as a worked example and
 * define the rest in YAML to avoid duplication.
 *
 * @author SmartShop Team
 */
@Configuration
public class RouteConfig {

    /**
     * Defines a programmatic route to the product-service.
     *
     * @param builder the Gateway-provided builder for assembling routes
     * @return a {@link RouteLocator} contributing the product route
     */
    @Bean
    public RouteLocator programmaticRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                // Example: all /api/products/** traffic -> product-service.
                .route("product-service-java", r -> r
                        // PREDICATE: match the path prefix.
                        .path("/api/products/**")
                        // FILTER: remove the leading "/api" segment so the
                        // downstream service sees "/products/**" (its real path).
                        .filters(f -> f.stripPrefix(1))
                        // DESTINATION: load-balanced lookup via Eureka.
                        .uri("lb://product-service"))
                .build();
    }
}
