package com.smartshop.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig - Configures OpenAPI metadata for order-service.
 *
 * <h2>Purpose</h2>
 * OpenAPI gives checkout consumers a documented contract for creating orders and reading Saga status. It reduces
 * integration friction in a microservice architecture with independently deployed services.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Swagger UI: Interactive browser documentation.</li>
 *   <li>Schema generation: DTO records become documented request and response models.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Springdoc serves this metadata at `/v3/api-docs` and UI at `/swagger-ui.html`.
 *
 * @see OpenAPI
 * @author SmartShop Team
 */
@Configuration
public class OpenApiConfig {
    /**
     * Builds order-service OpenAPI metadata.
     *
     * @return OpenAPI document
     */
    @Bean
    public OpenAPI orderOpenApi() {
        return new OpenAPI().info(new Info().title("SmartShop Order Service").version("1.0.0").description("Checkout and Saga APIs"));
    }
}
