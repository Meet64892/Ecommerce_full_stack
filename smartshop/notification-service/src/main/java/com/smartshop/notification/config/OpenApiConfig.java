package com.smartshop.notification.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig - Configures OpenAPI metadata for notification-service.
 *
 * <h2>Purpose</h2>
 * Notification-service has mostly asynchronous Kafka inputs, but OpenAPI metadata still documents actuator and any
 * future administrative endpoints. It keeps every SmartShop service consistent at `/swagger-ui.html`.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Consistency: All services expose Swagger UI even when their main interface is event-driven.</li>
 *   <li>Documentation: Operators can discover service purpose and version.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Springdoc serves this metadata using the web starter included in notification-service.
 *
 * @see OpenAPI
 * @author SmartShop Team
 */
@Configuration
public class OpenApiConfig {
    /**
     * Builds notification-service OpenAPI metadata.
     *
     * @return OpenAPI document
     */
    @Bean
    public OpenAPI notificationOpenApi() {
        return new OpenAPI().info(new Info().title("SmartShop Notification Service").version("1.0.0").description("Asynchronous notification workers"));
    }
}
