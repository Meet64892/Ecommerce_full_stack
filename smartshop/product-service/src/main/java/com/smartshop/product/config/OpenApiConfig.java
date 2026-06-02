package com.smartshop.product.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig - Product service OpenAPI metadata.
 *
 * <h2>Purpose</h2>
 * OpenAPI contracts make service capabilities discoverable and testable without reading source.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Contract-first documentation: standardized endpoint and schema metadata.</li>
 *   <li>Swagger UI: interactive docs for faster onboarding.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Serves API docs at /swagger-ui.html for product endpoints.
 *
 * @see org.springdoc.core
 * @author SmartShop Team
 */
@Configuration
public class OpenApiConfig {

    /**
     * Builds OpenAPI info object.
     *
     * @return OpenAPI bean
     */
    @Bean
    public OpenAPI productApi() {
        return new OpenAPI().info(new Info().title("SmartShop Product API").version("1.0.0"));
    }
}
