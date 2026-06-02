package com.smartshop.user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig - Configures the OpenAPI 3 document + Swagger UI for this service.
 *
 * <h2>Purpose</h2>
 * OpenAPI 3.0 is a language-agnostic, machine-readable contract describing every
 * endpoint, its parameters, and its responses. It matters because it gives us a
 * single source of truth that powers interactive docs (Swagger UI), client SDK
 * generation, and contract testing — keeping docs and code in lockstep.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Bearer security scheme</b>: declaring it here makes Swagger UI show an
 *       "Authorize" button so testers can paste a JWT and call protected
 *       endpoints.</li>
 *   <li>springdoc scans controllers at runtime to build the spec served at
 *       {@code /v3/api-docs} and rendered at {@code /swagger-ui.html}.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Auto-discovered by springdoc; combined with the {@code @Operation} annotations
 * on controllers to produce the full API documentation.
 *
 * @author SmartShop Team
 */
@Configuration
public class OpenApiConfig {

    /** Name of the security scheme referenced by protected operations. */
    private static final String BEARER_SCHEME = "bearerAuth";

    /**
     * Builds the OpenAPI definition including JWT bearer auth.
     *
     * @return the customized {@link OpenAPI} bean
     */
    @Bean
    public OpenAPI userServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("SmartShop User Service API")
                        .version("1.0.0")
                        .description("Authentication, JWT issuance, and user management."))
                // Apply the bearer requirement globally so the Authorize button works.
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components().addSecuritySchemes(BEARER_SCHEME,
                        new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
