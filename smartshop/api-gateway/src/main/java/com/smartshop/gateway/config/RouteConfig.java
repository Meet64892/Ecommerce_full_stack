package com.smartshop.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RouteConfig - Demonstrates programmatic Spring Cloud Gateway routes.
 *
 * <h2>Purpose</h2>
 * Gateway routes can be declared in YAML for operations-friendly configuration or in Java when composition and
 * type safety are useful. This class intentionally mirrors the YAML approach so learners can compare both styles.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Predicate: A rule such as Path=/products/** that decides whether a route matches.</li>
 *   <li>Filter: A route-specific transformation such as stripping a path prefix or adding a circuit breaker.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Spring merges these Java routes with application.yml routes and sends matching requests to Eureka service ids.
 *
 * @see RateLimiterConfig
 * @author SmartShop Team
 */
@Configuration
public class RouteConfig {
    /**
     * Defines sample Java routes that show the same concepts as YAML route definitions.
     *
     * @param builder fluent builder supplied by Spring Cloud Gateway
     * @return route locator used by the gateway runtime
     */
    @Bean
    public RouteLocator programmaticRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("programmatic-user-docs", route -> route
                        .path("/docs/users/**")
                        .filters(filters -> filters.rewritePath("/docs/users/(?<segment>.*)", "/v3/api-docs/${segment}"))
                        .uri("lb://user-service"))
                .build();
    }
}
