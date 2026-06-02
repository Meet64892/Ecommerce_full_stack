package com.smartshop.inventory.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * KafkaConfig - Configures inventory Kafka consumption and replies.
 *
 * <h2>Purpose</h2>
 * Inventory listens for order-created events and publishes reservation results. Kafka gives durable buffering so
 * temporary inventory downtime does not require order-service to retry synchronous HTTP calls.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Consumer group: Instances share work by group id.</li>
 *   <li>Trusted packages: JsonDeserializer restricts which classes can be deserialized for safety.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * OrderEventConsumer uses listener infrastructure and KafkaTemplate configured here.
 *
 * @see com.smartshop.inventory.kafka.OrderEventConsumer
 * @author SmartShop Team
 */
@Configuration
public class KafkaConfig {
    private final String bootstrapServers;

    /**
     * Captures Kafka brokers from configuration.
     *
     * @param bootstrapServers Kafka bootstrap server list
     */
    public KafkaConfig(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    /**
     * Builds JSON producer settings for inventory replies.
     *
     * @return producer factory
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * Creates KafkaTemplate for publishing inventory.checked.
     *
     * @return Kafka template
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Builds JSON consumer settings for order.created events.
     *
     * @return consumer factory
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.smartshop.*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.smartshop.inventory.kafka.OrderCreatedEvent");
        return new DefaultKafkaConsumerFactory<>(props);
    }
}
