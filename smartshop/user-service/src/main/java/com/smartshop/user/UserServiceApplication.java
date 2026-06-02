package com.smartshop.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * UserServiceApplication - User Management and Authentication Service
 *
 * <h2>Purpose</h2>
 * This service owns the User domain: registration, authentication, profile management,
 * and JWT token issuance. It is the ONLY service that issues JWTs. All other services
 * receive JWTs (already verified by the gateway) but never create them.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>@EnableJpaAuditing: Activates Spring Data JPA's auditing infrastructure.
 *       When enabled, @CreatedDate and @LastModifiedDate fields on entities are
 *       automatically populated by Spring before every save/update.
 *       Without this annotation on the @SpringBootApplication class (or a @Configuration
 *       class), the auditing annotations on entities are silently ignored — fields stay null.</li>
 *   <li>Single Responsibility: This service ONLY handles users and auth.
 *       Other services call this service's endpoints (or trust the gateway's X-User-Id header)
 *       to get user context — they do NOT store user data themselves.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@SpringBootApplication
@EnableDiscoveryClient
// @EnableJpaAuditing activates the AuditingEntityListener on entities annotated
// with @EntityListeners(AuditingEntityListener.class).
// auditorAwareRef points to the AuditorAware<String> bean in AuditConfig that
// provides the "current user" name for @CreatedBy/@LastModifiedBy fields.
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
