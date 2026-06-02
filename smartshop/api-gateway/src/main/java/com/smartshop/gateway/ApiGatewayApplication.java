package com.smartshop.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * ApiGatewayApplication - The Single Entry Point for All SmartShop Traffic
 *
 * <h2>Purpose</h2>
 * The API Gateway is the architectural pattern of providing a single entry point
 * for all client requests. Instead of clients knowing the address of each microservice,
 * they call ONE URL (the gateway) and it routes requests to the appropriate service.
 *
 * Without a gateway, every client would need to:
 *   - Know the URL of every service
 *   - Implement JWT validation in every client
 *   - Handle cross-cutting concerns (CORS, rate limiting, logging) themselves
 *   - Deal with service discovery directly
 *
 * The gateway centralizes all of this, keeping microservices focused on business logic.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Spring Cloud Gateway vs Zuul: Spring Cloud Gateway is built on Spring WebFlux
 *       (non-blocking, reactive). Zuul 1.x (Netflix's older gateway) is blocking.
 *       At scale, Gateway can handle 5-10x more concurrent connections with the
 *       same hardware because it never blocks threads waiting for upstream responses.</li>
 *   <li>Predicates: Conditions that determine if a route matches a request.
 *       Example: Path("/api/users/**") only matches requests to /api/users/...</li>
 *   <li>Filters: Transformations applied to requests/responses.
 *       Filters form a chain — each filter can modify the request, pass it forward,
 *       or short-circuit the chain (e.g., return 401 if JWT is invalid).</li>
 *   <li>Cross-Cutting Concerns: Functionality needed by every service but not
 *       specific to any business domain: authentication, logging, rate limiting,
 *       tracing, CORS. The gateway is the ideal place for these.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * All external HTTP requests arrive here. The gateway:
 *   1. Logs the request with a correlation ID (LoggingFilter)
 *   2. Validates the JWT token (AuthenticationFilter)
 *   3. Checks rate limits (RequestRateLimiter)
 *   4. Routes to the correct service (via Eureka load-balanced lb:// URLs)
 *   5. Falls back to error endpoints if the upstream service is down (CircuitBreaker)
 *
 * @author SmartShop Team
 */
@SpringBootApplication
// EnableDiscoveryClient: registers this gateway with Eureka and enables
// load-balanced routing via lb://service-name URLs
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
