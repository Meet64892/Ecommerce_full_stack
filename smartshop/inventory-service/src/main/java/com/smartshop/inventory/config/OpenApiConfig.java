package com.smartshop.inventory.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig - Configures OpenAPI metadata for inventory-service.
 *
 * <h2>Purpose</h2>
 * OpenAPI documents inventory reads and reservations so consumers understand optimistic-locking behavior and response
 * shapes. Swagger UI provides a lightweight manual testing surface.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>API contract: Keeps gateway and client integrations aligned.</li>
 *   <li>Interactive docs: Developers can try endpoints from the browser.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Springdoc serves generated docs at `/v3/api-docs` and UI at `/swagger-ui.html`.
 *
 * @see OpenAPI
 * @author SmartShop Team
 */
@Configuration
public class OpenApiConfig {
    /**
     * Builds inventory-service OpenAPI metadata.
     *
     * @return OpenAPI document
     */
    @Bean
    public OpenAPI inventoryOpenApi() {
        return new OpenAPI().info(new Info().title("SmartShop Inventory Service").version("1.0.0").description("Stock and reservation APIs"));
    }
}
