package com.smartshop.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ApiGatewayApplication - SmartShop API gateway bootstrap.
 *
 * <h2>Purpose</h2>
 * The gateway acts as the single entry point for external clients. It centralizes cross-cutting
 * concerns such as authentication, observability, resilience, and traffic shaping so business
 * services remain focused on domain logic.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Reactive routing: Spring Cloud Gateway is non-blocking and built on Project Reactor.</li>
 *   <li>Cross-cutting concerns: auth, logging, and rate limiting are applied consistently.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Clients call the gateway, which resolves service targets via Eureka and forwards requests.
 *
 * @see com.smartshop.gateway.config.RouteConfig
 * @author SmartShop Team
 */
@SpringBootApplication
public class ApiGatewayApplication {

    /**
     * Starts the API gateway service.
     *
     * @param args process arguments
     * @return nothing because Spring manages process lifecycle
     */
    public static void main(final String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
