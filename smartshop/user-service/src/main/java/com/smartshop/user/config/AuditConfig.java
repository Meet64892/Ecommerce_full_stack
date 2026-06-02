package com.smartshop.user.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * AuditConfig - Enables Spring Data JPA auditing support.
 *
 * <h2>Purpose</h2>
 * @EnableJpaAuditing activates infrastructure that automatically populates @CreatedDate,
 * @LastModifiedDate, and @CreatedBy fields, reducing repetitive manual timestamp code.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>AuditorAware: strategy for resolving current actor identity.</li>
 *   <li>Entity listeners: hooks write audit fields during persistence lifecycle events.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Applied globally so audited entities like User are populated consistently.
 *
 * @see com.smartshop.user.entity.User
 * @author SmartShop Team
 */
@Configuration
@EnableJpaAuditing
public class AuditConfig {

    /**
     * Returns current actor identifier for createdBy fields.
     *
     * @return auditor provider bean
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("system");
    }
}
