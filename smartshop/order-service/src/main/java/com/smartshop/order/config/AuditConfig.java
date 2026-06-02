package com.smartshop.order.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * AuditConfig - Enables JPA auditing in order service.
 *
 * <h2>Purpose</h2>
 * Automatically fills audit fields on Order entities, making state transition history reliable.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>@EnableJpaAuditing activates audit infrastructure globally.</li>
 *   <li>AuditorAware resolves creator identity for createdBy field.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Supports auditing annotations in Order entity.
 *
 * @see com.smartshop.order.entity.Order
 * @author SmartShop Team
 */
@Configuration
@EnableJpaAuditing
public class AuditConfig {

    /**
     * Provides fallback auditor identity.
     *
     * @return auditor provider
     */
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("system");
    }
}
