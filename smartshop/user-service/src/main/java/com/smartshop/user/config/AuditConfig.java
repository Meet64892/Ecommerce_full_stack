package com.smartshop.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * AuditConfig - Spring Data JPA Auditing Configuration
 *
 * <h2>Purpose</h2>
 * Provides the AuditorAware bean that Spring Data JPA uses to populate
 * @CreatedBy and @LastModifiedBy fields on entities. When a user creates or
 * updates a record, these fields automatically store who made the change.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>@EnableJpaAuditing (on UserServiceApplication): Activates the auditing
 *       infrastructure. Without this, @CreatedDate and @CreatedBy are silently ignored.</li>
 *   <li>AuditorAware{@code <String>}: Generic interface for providing the current auditor.
 *       Spring calls getCurrentAuditor() before every save/update to get the username.
 *       The generic parameter is the type of the @CreatedBy field (String here).</li>
 *   <li>SecurityContextHolder: Spring Security's thread-local storage for the current
 *       authentication. In a stateless JWT setup, it's populated by JwtAuthFilter
 *       before the service layer is invoked.</li>
 *   <li>Optional.of("system"): When no authentication exists (e.g., during self-registration
 *       before the user has a JWT), we fall back to "system" as the auditor.
 *       Returning Optional.empty() would leave @CreatedBy fields null.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Configuration
public class AuditConfig {

    /**
     * Provides the username of the current authenticated user for JPA auditing.
     * This bean is referenced by @EnableJpaAuditing(auditorAwareRef = "auditorAware").
     *
     * @return an AuditorAware that returns the current user's email, or "system"
     */
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            // Get the current authentication from Spring Security's thread-local context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Return "system" if no authentication is set (e.g., during registration)
            // or if the authentication is anonymous (public endpoint access)
            if (authentication == null || !authentication.isAuthenticated()
                    || authentication.getName().equals("anonymousUser")) {
                return Optional.of("system");
            }

            // The authentication name is the email (set by UserDetailsServiceImpl.loadUserByUsername)
            return Optional.of(authentication.getName());
        };
    }
}
