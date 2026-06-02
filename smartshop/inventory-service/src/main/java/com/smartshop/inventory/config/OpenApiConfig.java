package com.smartshop.inventory.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig - OpenAPI 3 metadata for the inventory-service.
 *
 * <h2>Purpose</h2>
 * Documents inventory endpoints in Swagger UI.
 *
 * <h2>How it fits in the system</h2>
 * Discovered by springdoc; served at {@code /swagger-ui.html}.
 *
 * @author SmartShop Team
 */
@Configuration
public class OpenApiConfig {

    /**
     * @return OpenAPI metadata bean for this service
     */
    @Bean
    public OpenAPI inventoryServiceOpenApi() {
        return new OpenAPI().info(new Info()
                .title("SmartShop Inventory Service API")
                .version("1.0.0")
                .description("Stock levels, reservations, Redis caching, optimistic locking."));
    }
}
