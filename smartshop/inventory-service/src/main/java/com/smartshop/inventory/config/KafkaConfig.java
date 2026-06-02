package com.smartshop.inventory.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * KafkaConfig - Declares the reply topic this service produces.
 *
 * <h2>Purpose</h2>
 * Ensures the {@code inventory.checked} reply topic exists. Consumer/producer
 * (de)serializers are configured via application.yml.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>This service is both a CONSUMER (order.created) and a PRODUCER
 *       (inventory.checked) — the request/reply halves of the saga step.</li>
 *   <li><b>Dead Letter Topic (DLT)</b>: messages that repeatedly fail processing
 *       can be routed to a DLT for later inspection instead of blocking the
 *       partition forever (see the error handler note in application.yml).</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * The reply topic is consumed by order-service's {@code InventoryResponseConsumer}.
 *
 * @author SmartShop Team
 */
@Configuration
public class KafkaConfig {

    /** Topic carrying reservation results back to the order saga. */
    public static final String INVENTORY_CHECKED = "inventory.checked";

    /**
     * @return the inventory.checked reply topic
     */
    @Bean
    public NewTopic inventoryCheckedTopic() {
        return TopicBuilder.name(INVENTORY_CHECKED).partitions(3).replicas(1).build();
    }
}
