package com.smartshop.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig - Order service OpenAPI metadata.
 *
 * <h2>Purpose</h2>
 * Documents order endpoints for consumers and testing tools.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Machine-readable contracts reduce integration ambiguity.</li>
 *   <li>Swagger UI supports exploratory testing.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Exposes docs at /swagger-ui.html.
 *
 * @see org.springdoc.core
 * @author SmartShop Team
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates OpenAPI info model.
     *
     * @return OpenAPI bean
     */
    @Bean
    public OpenAPI orderApi() {
        return new OpenAPI().info(new Info().title("SmartShop Order API").version("1.0.0"));
    }
}
