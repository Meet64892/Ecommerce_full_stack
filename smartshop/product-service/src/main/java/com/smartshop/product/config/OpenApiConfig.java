package com.smartshop.product.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig - OpenAPI 3 document metadata for the product-service.
 *
 * <h2>Purpose</h2>
 * Provides the title/version/description shown in Swagger UI. OpenAPI 3.0 gives
 * a machine-readable, language-agnostic contract that powers docs, client
 * generation, and contract tests from one source of truth.
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
    public OpenAPI productServiceOpenApi() {
        return new OpenAPI().info(new Info()
                .title("SmartShop Product Service API")
                .version("1.0.0")
                .description("Product catalog CRUD plus Elasticsearch-backed search."));
    }
}
