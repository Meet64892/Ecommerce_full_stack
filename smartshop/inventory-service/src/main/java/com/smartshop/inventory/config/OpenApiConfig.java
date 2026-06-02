package com.smartshop.inventory.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig - Inventory API documentation setup.
 *
 * <h2>Purpose</h2>
 * Documents inventory endpoints and payloads for API consumers.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>OpenAPI 3 standardization improves interoperability.</li>
 *   <li>Swagger UI enables quick manual verification.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Exposes interactive docs at /swagger-ui.html.
 *
 * @see org.springdoc.core
 * @author SmartShop Team
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates OpenAPI bean.
     *
     * @return OpenAPI metadata
     */
    @Bean
    public OpenAPI inventoryApi() {
        return new OpenAPI().info(new Info().title("SmartShop Inventory API").version("1.0.0"));
    }
}
