package com.smartshop.notification.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig - OpenAPI 3 metadata for the notification-service.
 *
 * <h2>Purpose</h2>
 * Even though this service is event-driven (no business REST endpoints), it still
 * exposes actuator + a Swagger UI describing its operational surface.
 *
 * <h2>How it fits in the system</h2>
 * Discovered by springdoc; served at {@code /swagger-ui.html}.
 *
 * @author SmartShop Team
 */
@Configuration
public class OpenApiConfig {

    /**
     * @return OpenAPI metadata bean for this service
     */
    @Bean
    public OpenAPI notificationServiceOpenApi() {
        return new OpenAPI().info(new Info()
                .title("SmartShop Notification Service API")
                .version("1.0.0")
                .description("Event-driven email notifications (Kafka consumer)."));
    }
}
