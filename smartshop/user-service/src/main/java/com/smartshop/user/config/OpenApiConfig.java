package com.smartshop.user.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenApiConfig - Swagger/OpenAPI 3.0 Documentation Configuration
 *
 * <h2>Purpose</h2>
 * Configures the interactive API documentation available at /swagger-ui.html.
 * OpenAPI 3.0 is the industry standard for documenting REST APIs.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>OpenAPI 3.0 (formerly Swagger): A specification for describing REST APIs in
 *       a standard JSON/YAML format. Tools can auto-generate:
 *       - Interactive documentation (SwaggerUI)
 *       - Client SDKs in any language (OpenAPI Generator)
 *       - Postman collections, API testing suites</li>
 *   <li>@SecurityScheme: Declares the authentication mechanism for Swagger UI.
 *       With this, the "Authorize" button in SwaggerUI lets you paste a JWT token
 *       that gets added to all API requests from the UI.</li>
 *   <li>springdoc-openapi: The library that auto-generates OpenAPI spec by scanning
 *       your @RestController classes and @Operation/@ApiResponse annotations.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Configuration
// Declares the JWT Bearer auth scheme for Swagger UI
// Type HTTP, scheme "bearer", bearerFormat "JWT" — adds the Authorize button
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Enter your JWT token (without 'Bearer ' prefix). " +
                  "Get it from POST /auth/login or POST /auth/register"
)
public class OpenApiConfig {

    /**
     * Configures the OpenAPI metadata shown at the top of the Swagger UI page.
     *
     * @return the OpenAPI configuration object
     */
    @Bean
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SmartShop User Service API")
                        .description("REST API for user management and JWT authentication. " +
                                     "Use POST /auth/register or POST /auth/login to get a token, " +
                                     "then click 'Authorize' to use protected endpoints.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SmartShop Team")
                                .email("dev@smartshop.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                )
                .servers(List.of(
                        // Direct access (development/testing)
                        new Server().url("http://localhost:8081").description("User Service (direct)"),
                        // Via API Gateway (recommended for production testing)
                        new Server().url("http://localhost:8080/api").description("Via API Gateway")
                ));
    }
}
