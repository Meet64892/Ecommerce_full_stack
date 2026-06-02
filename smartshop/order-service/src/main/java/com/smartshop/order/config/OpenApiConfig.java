package com.smartshop.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig - OpenAPI 3 metadata for the order-service.
 *
 * <h2>Purpose</h2>
 * Documents the order API in Swagger UI from a single machine-readable spec.
 *
 * <h2>How it fits in the system</h2>
 * Discovered by springdoc; served at {@code /swagger-ui.html}.
 *
 * @author SmartShop Team
 */
@Configuration
public class OpenApiConfig {

    /**
     * @return the OpenAPI metadata bean for this service
     */
    @Bean
    public OpenAPI orderServiceOpenApi() {
        return new OpenAPI().info(new Info()
                .title("SmartShop Order Service API")
                .version("1.0.0")
                .description("Order placement and lifecycle, orchestrated via the Saga pattern."));
    }
}
