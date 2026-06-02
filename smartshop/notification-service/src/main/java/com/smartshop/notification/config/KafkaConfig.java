package com.smartshop.notification.config;

import org.springframework.context.annotation.Configuration;

/**
 * KafkaConfig - Marker config for notification consumer settings.
 *
 * <h2>Purpose</h2>
 * Consumer (de)serializers, group id, and trusted packages are configured in
 * application.yml. This class documents the consumer-group semantics and the
 * dead-letter concept; no beans are required because we consume topics owned by
 * other services.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Consumer groups</b>: this service uses its OWN group
 *       ({@code notification-service}). Because each group independently
 *       receives every message, notification gets order.confirmed even though
 *       (a different group) someone else might too — groups don't steal messages
 *       from each other.</li>
 *   <li><b>Dead Letter Topic (DLT)</b>: if an event repeatedly fails to process
 *       (e.g. a malformed payload), a DLT lets us shunt it aside after N attempts
 *       so it stops blocking the partition, while preserving it for inspection
 *       and reprocessing. Configure via a DefaultErrorHandler + DeadLetter
 *       PublishingRecoverer in a hardened setup.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Backs the {@code @KafkaListener}s in the consumer classes.
 *
 * @author SmartShop Team
 */
@Configuration
public class KafkaConfig {
    // Intentionally empty: consumer configuration is externalized to YAML.
    // Kept as a documented anchor for Kafka concepts in this service.
}
