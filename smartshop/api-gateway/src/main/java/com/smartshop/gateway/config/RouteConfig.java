package com.smartshop.gateway.config;

import com.smartshop.gateway.filter.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RouteConfig - Programmatic Route Definitions for the API Gateway
 *
 * <h2>Purpose</h2>
 * This class defines the same routes as application.yml but using Java code.
 * The purpose of showing BOTH approaches:
 *   - YAML routes (in application.yml): Simple cases, quick to write, no recompile needed
 *   - Java routes (here): Complex logic, conditions, dynamic predicates, type-safe
 *
 * This is an EDUCATIONAL class — in practice, pick ONE approach and stick with it.
 * Mixing both works (Spring Cloud Gateway merges them) but can be confusing.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>RouteLocatorBuilder: Fluent builder API for defining routes programmatically.
 *       Equivalent to the spring.cloud.gateway.routes YAML config but in Java.</li>
 *   <li>lb://service-name: The "lb" scheme tells the gateway to use load-balanced
 *       routing via Eureka. Instead of a hardcoded URL, it discovers the service
 *       instances from the registry and distributes traffic across them.</li>
 *   <li>Predicates: Route matching conditions. Examples:
 *       .path("/api/users/**") — match any path starting with /api/users/
 *       .method("GET", "POST") — match only GET and POST
 *       .header("X-Custom-Header", "value") — match specific header value</li>
 *   <li>Filters: Request/response transformations. Examples:
 *       .stripPrefix(1) — removes the first path segment (/api/users → /users)
 *       .addRequestHeader("X-Source", "gateway") — adds a header to upstream request
 *       .retry(3) — retries the upstream call up to 3 times on failure</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class RouteConfig {

    // AuthenticationFilter is injected to apply JWT validation on secured routes
    private final AuthenticationFilter authenticationFilter;

    /**
     * Defines all application routes programmatically.
     * Routes are evaluated in ORDER — the first matching route wins.
     * Put more specific routes (longer paths) BEFORE less specific ones.
     *
     * @param builder the RouteLocatorBuilder provided by Spring Cloud Gateway
     * @return a RouteLocator containing all configured routes
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // =============================================================
                // USER SERVICE ROUTES (/api/auth/** and /api/users/**)
                // =============================================================
                .route("user-service-auth", r -> r
                        // Match all requests to /api/auth/** (register, login — no auth needed)
                        .path("/api/auth/**")
                        .filters(f -> f
                                // stripPrefix(1) removes the first path segment.
                                // /api/auth/login → /auth/login (what user-service expects)
                                // Without this, user-service would receive /api/auth/login
                                // but its controller is mapped to /auth/login
                                .stripPrefix(1)
                                // Add a header identifying that this request came through the gateway
                                // Downstream services can use this to reject direct calls (bypass gateway)
                                .addRequestHeader("X-Forwarded-By", "smartshop-gateway")
                        )
                        // lb:// = load-balanced; looks up "user-service" in Eureka registry
                        .uri("lb://user-service")
                )

                .route("user-service-users", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                // Apply JWT authentication — must be logged in to access user data
                                .filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                                .addRequestHeader("X-Forwarded-By", "smartshop-gateway")
                        )
                        .uri("lb://user-service")
                )

                // =============================================================
                // PRODUCT SERVICE ROUTES (/api/products/** and /api/categories/**)
                // =============================================================
                .route("product-service", r -> r
                        .path("/api/products/**", "/api/categories/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Forwarded-By", "smartshop-gateway")
                                // CircuitBreaker: if product-service is down, fall back to /fallback/products
                                // This prevents the gateway from hanging waiting for an unavailable service
                                .circuitBreaker(config -> config
                                        .setName("productServiceCB")
                                        .setFallbackUri("forward:/fallback/products"))
                        )
                        .uri("lb://product-service")
                )

                // =============================================================
                // ORDER SERVICE ROUTES (/api/orders/**)
                // =============================================================
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                                .addRequestHeader("X-Forwarded-By", "smartshop-gateway")
                                .circuitBreaker(config -> config
                                        .setName("orderServiceCB")
                                        .setFallbackUri("forward:/fallback/orders"))
                                // Retry: retry up to 2 times on connection failure
                                // (NOT on 4xx/5xx responses — those are legitimate errors)
                                .retry(retryConfig -> retryConfig
                                        .setRetries(2)
                                        .setMethods(
                                                org.springframework.http.HttpMethod.GET))
                        )
                        .uri("lb://order-service")
                )

                // =============================================================
                // INVENTORY SERVICE ROUTES (/api/inventory/**)
                // =============================================================
                .route("inventory-service", r -> r
                        .path("/api/inventory/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Forwarded-By", "smartshop-gateway")
                        )
                        .uri("lb://inventory-service")
                )

                .build();
    }
}
