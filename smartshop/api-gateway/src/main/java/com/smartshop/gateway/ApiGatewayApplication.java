package com.smartshop.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ApiGatewayApplication - The single front door for all client traffic.
 *
 * <h2>Purpose</h2>
 * An API gateway gives clients ONE address ({@code :8080}) instead of forcing
 * them to know the host/port of five backend services. It is also the natural
 * home for <i>cross-cutting concerns</i>: authentication, rate limiting,
 * logging/correlation, and circuit breaking — implemented once at the edge
 * rather than copied into every service.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Spring Cloud Gateway vs Zuul 1</b>: Gateway is built on the reactive,
 *       non-blocking WebFlux/Netty stack, so a single thread handles many
 *       concurrent connections efficiently. Zuul 1 used a blocking thread-per-
 *       request model that scaled poorly under high I/O fan-out.</li>
 *   <li><b>Routes = predicates + filters</b>: a route says "IF the request
 *       matches these predicates (path, method, header...), THEN apply these
 *       filters and forward to this destination".</li>
 *   <li><b>lb:// load balancing</b>: combined with the Eureka client, the
 *       gateway resolves {@code lb://user-service} to a live instance.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Clients call the gateway; it authenticates the JWT, stamps a correlation id,
 * rate-limits, and forwards to the appropriate downstream service discovered via
 * Eureka — falling back gracefully when a service is unhealthy.
 *
 * @author SmartShop Team
 */
@SpringBootApplication
public class ApiGatewayApplication {

    /**
     * Spring Boot entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
