package com.smartshop.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * AuditConfig - Enables auditing for order entities.
 *
 * <h2>Purpose</h2>
 * Order records need creation and modification metadata for support and compliance. @EnableJpaAuditing activates the
 * entity listener that fills @CreatedDate, @LastModifiedDate, and @CreatedBy fields.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>AuditorAware: Supplies the current actor for audit fields.</li>
 *   <li>CreatedDate: Captures when the order entered the system.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Order entity annotations are populated whenever repositories persist aggregates.
 *
 * @see com.smartshop.order.entity.Order
 * @author SmartShop Team
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "orderAuditorAware")
public class AuditConfig {
    /**
     * Returns a simple system auditor because gateway identity propagation is outside this demo's order persistence path.
     *
     * @return auditor provider
     */
    @Bean
    public AuditorAware<String> orderAuditorAware() {
        return () -> Optional.of("system");
    }
}
