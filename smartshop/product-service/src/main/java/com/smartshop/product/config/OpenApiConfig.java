package com.smartshop.product.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig - Configures OpenAPI metadata for product-service.
 *
 * <h2>Purpose</h2>
 * OpenAPI documents the contract for catalog APIs so humans and tools can understand supported endpoints, schemas,
 * and status codes. This matters in microservices because teams often integrate without reading service internals.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Swagger UI: Browser-based explorer generated from `/v3/api-docs`.</li>
 *   <li>Contract-first thinking: API shape is explicit and reviewable.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Springdoc combines this bean with controller annotations to serve `/swagger-ui.html`.
 *
 * @see OpenAPI
 * @author SmartShop Team
 */
@Configuration
public class OpenApiConfig {
    /**
     * Builds the OpenAPI metadata document.
     *
     * @return OpenAPI document used by Swagger UI
     */
    @Bean
    public OpenAPI productOpenApi() {
        return new OpenAPI().info(new Info().title("SmartShop Product Service").version("1.0.0").description("Catalog and search APIs"));
    }
}
