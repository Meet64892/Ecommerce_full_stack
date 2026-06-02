package com.smartshop.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ApiGatewayApplication - Starts the reactive edge service for SmartShop.
 *
 * <h2>Purpose</h2>
 * An API Gateway gives clients one stable entry point while internal services remain independently deployed.
 * Cross-cutting concerns such as authentication, rate limiting, correlation IDs, and circuit breaking are handled
 * once at the edge instead of being duplicated in every service.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Reactive gateway: Spring Cloud Gateway is non-blocking, unlike the older blocking Zuul 1 model.</li>
 *   <li>Filter chain: Requests pass through ordered filters before being routed to downstream services.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * External clients call this application, which routes to Eureka-discovered services using `lb://` URIs.
 *
 * @see org.springframework.cloud.gateway.route.RouteLocator
 * @author SmartShop Team
 */
@SpringBootApplication
public class ApiGatewayApplication {
    /**
     * Starts the gateway and its reactive Netty server.
     *
     * @param args command-line arguments passed by the runtime
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
