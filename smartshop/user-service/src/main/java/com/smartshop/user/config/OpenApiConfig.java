package com.smartshop.user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig - Swagger/OpenAPI 3 documentation setup.
 *
 * <h2>Purpose</h2>
 * OpenAPI 3 defines machine-readable API contracts that drive documentation, SDK generation, and
 * contract governance across teams.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Security scheme: declares JWT bearer auth in generated docs.</li>
 *   <li>Interactive docs: Swagger UI enables rapid endpoint exploration.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Exposes documented endpoints at /swagger-ui.html for user-service APIs.
 *
 * @see org.springdoc.core
 * @author SmartShop Team
 */
@Configuration
public class OpenApiConfig {

    /**
     * Builds OpenAPI model with bearer auth requirement.
     *
     * @return OpenAPI specification bean
     */
    @Bean
    public OpenAPI userServiceOpenApi() {
        return new OpenAPI()
                .info(new Info().title("SmartShop User Service API").version("1.0.0"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
