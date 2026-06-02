package com.smartshop.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * AuditConfig - Enables automatic population of JPA audit fields.
 *
 * <h2>Purpose</h2>
 * Audit metadata answers who created a row and when it changed, which is essential for security investigations and
 * compliance. @EnableJpaAuditing activates Spring Data listeners that fill @CreatedDate and @LastModifiedDate fields.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>AuditorAware: Supplies the current username for @CreatedBy fields.</li>
 *   <li>Entity listener: Hibernate calls auditing hooks during persistence lifecycle events.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * User entity audit annotations depend on this configuration while repositories save entities normally.
 *
 * @see com.smartshop.user.entity.User
 * @author SmartShop Team
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AuditConfig {
    /**
     * Resolves the current auditor from Spring Security or falls back to system for unauthenticated registration.
     *
     * @return auditor provider used by Spring Data JPA
     */
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getName)
                .or(() -> Optional.of("system"));
    }
}
