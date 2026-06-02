package com.smartshop.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * UserServiceApplication - Bootstraps the authentication & user-management service.
 *
 * <h2>Purpose</h2>
 * Owns the "users" bounded context: registration, login (issuing JWTs), and
 * profile CRUD. It is the identity authority other services trust (the gateway
 * verifies tokens this service signs).
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@code @SpringBootApplication}: composite of {@code @Configuration},
 *       {@code @EnableAutoConfiguration}, {@code @ComponentScan}.</li>
 *   <li>{@code @EnableJpaAuditing}: activates Spring Data JPA auditing so
 *       {@code @CreatedDate}/{@code @LastModifiedDate}/{@code @CreatedBy} fields
 *       are populated automatically on save/update — no manual timestamp code.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Registers in Eureka as {@code user-service}; the gateway routes
 * {@code /api/users/**} here after stripping the prefix.
 *
 * @author SmartShop Team
 */
@SpringBootApplication
// Turns on automatic auditing population for entities annotated with auditing
// fields. The AuditorAware bean (see AuditConfig) supplies @CreatedBy values.
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class UserServiceApplication {

    /**
     * Spring Boot entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
