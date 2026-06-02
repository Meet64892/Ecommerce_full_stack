package com.smartshop.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
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
 * KafkaConfig - Configures Kafka producers, consumers, and topics for order-service.
 *
 * <h2>Purpose</h2>
 * Kafka topics are durable append-only logs. Producers write records, consumers read by group, and offsets track what
 * each group has processed so services can restart without losing workflow progress.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>At-least-once delivery: Retries can duplicate records, so consumers must be idempotent.</li>
 *   <li>Dead letter topics: Production systems route repeatedly failing records to a separate topic for inspection.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * OrderEventProducer uses KafkaTemplate and InventoryResponseConsumer uses the listener container infrastructure.
 *
 * @see com.smartshop.order.kafka.OrderEventProducer
 * @author SmartShop Team
 */
@Configuration
public class KafkaConfig {
    private final String bootstrapServers;

    /**
     * Captures Kafka bootstrap servers from configuration.
     *
     * @param bootstrapServers comma-separated Kafka brokers
     */
    public KafkaConfig(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    /**
     * Creates producer settings for JSON events.
     *
     * @return producer factory used by KafkaTemplate
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
     * Creates the KafkaTemplate used by producers.
     *
     * @return KafkaTemplate for string keys and JSON values
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Creates consumer settings for JSON events.
     *
     * @return consumer factory used by @KafkaListener
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.smartshop.*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.smartshop.order.event.InventoryCheckedEvent");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * Declares the order-created topic for local development.
     *
     * @return topic definition
     */
    @Bean
    public NewTopic orderCreatedTopic() { return TopicBuilder.name("order.created").partitions(3).replicas(1).build(); }
    /** @return topic definition for inventory replies */ @Bean public NewTopic inventoryCheckedTopic() { return TopicBuilder.name("inventory.checked").partitions(3).replicas(1).build(); }
    /** @return topic definition for confirmed orders */ @Bean public NewTopic orderConfirmedTopic() { return TopicBuilder.name("order.confirmed").partitions(3).replicas(1).build(); }
    /** @return topic definition for cancelled orders */ @Bean public NewTopic orderCancelledTopic() { return TopicBuilder.name("order.cancelled").partitions(3).replicas(1).build(); }
}
