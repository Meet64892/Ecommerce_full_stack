package com.smartshop.notification.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * KafkaConfig - Configures notification-service Kafka consumers.
 *
 * <h2>Purpose</h2>
 * Notification-service consumes event payloads as raw JSON strings so it can parse local DTOs per topic. This keeps the
 * service loosely coupled to producers and makes dead-letter handling easier to add later.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Consumer groups: Every group receives the full stream independently.</li>
 *   <li>Dead letter topics: Failed records can be routed to `*.DLT` topics after retries in production.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * OrderEventConsumer and UserEventConsumer use the consumer factory created here.
 *
 * @see com.smartshop.notification.kafka.OrderEventConsumer
 * @author SmartShop Team
 */
@Configuration
public class KafkaConfig {
    private final String bootstrapServers;

    /**
     * Captures Kafka bootstrap servers from configuration.
     *
     * @param bootstrapServers comma-separated broker list
     */
    public KafkaConfig(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    /**
     * Creates a String payload consumer factory.
     *
     * @return consumer factory used by listener containers
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(props);
    }
}
