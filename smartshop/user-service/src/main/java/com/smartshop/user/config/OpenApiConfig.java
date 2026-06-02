package com.smartshop.user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig - Configures Swagger/OpenAPI metadata for user-service.
 *
 * <h2>Purpose</h2>
 * OpenAPI 3.0 describes HTTP APIs in a machine-readable format. Swagger UI uses that contract to render interactive
 * documentation so developers can learn and test endpoints without reading controller code first.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Bearer auth scheme: Documents that protected endpoints require an Authorization header.</li>
 *   <li>API contract: Tools can generate clients, tests, and docs from the same source.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Springdoc scans controller annotations and merges them with this metadata at `/v3/api-docs`.
 *
 * @see io.swagger.v3.oas.models.OpenAPI
 * @author SmartShop Team
 */
@Configuration
public class OpenApiConfig {
    /**
     * Builds the OpenAPI document and registers JWT bearer authentication.
     *
     * @return OpenAPI metadata used by Swagger UI
     */
    @Bean
    public OpenAPI userServiceOpenApi() {
        String schemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info().title("SmartShop User Service").version("1.0.0").description("Authentication and user profiles"))
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components().addSecuritySchemes(schemeName,
                        new SecurityScheme().name(schemeName).type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
