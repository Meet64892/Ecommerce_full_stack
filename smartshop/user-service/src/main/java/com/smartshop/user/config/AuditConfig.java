package com.smartshop.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * AuditConfig - Supplies the "who" for JPA auditing.
 *
 * <h2>Purpose</h2>
 * {@code @CreatedBy} needs to know the current principal. This {@link AuditorAware}
 * reads the authenticated username from the Spring Security context so audit
 * columns are populated automatically.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@code @EnableJpaAuditing(auditorAwareRef = "auditorAware")} on the
 *       application class references this bean by name.</li>
 *   <li>Returns "system" when there is no authenticated user (e.g. during
 *       registration, before the principal exists).</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Invoked by the auditing entity listener whenever a {@code User} is saved.
 *
 * @author SmartShop Team
 */
@Configuration
public class AuditConfig {

    /**
     * Resolves the current auditor (username) for {@code @CreatedBy}.
     *
     * @return an {@link AuditorAware} reading the security context
     */
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            // No principal yet (anonymous / system action) -> attribute to "system".
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return Optional.of("system");
            }
            return Optional.of(auth.getName());
        };
    }
}
