package com.smartshop.order.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

/**
 * KafkaConfig - Kafka producer/consumer and topic definitions.
 *
 * <h2>Purpose</h2>
 * Configures event infrastructure for saga messaging. Kafka offers durable, partitioned, and
 * replayable streams with consumer offsets for robust asynchronous workflows.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Topic partitions: scale consumers horizontally with ordered partitions.</li>
 *   <li>At-least-once delivery: consumers must be idempotent to handle retries safely.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Provides templates and listener factories used by producers/consumers.
 *
 * @see org.springframework.kafka.annotation.KafkaListener
 * @author SmartShop Team
 */
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Producer factory for JSON event publication.
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
     * Kafka template for sending events.
     *
     * @return kafka template
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Consumer factory for JSON event ingestion.
     *
     * @return consumer factory
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        final JsonDeserializer<Object> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("com.smartshop.*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-service-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    /**
     * Listener container factory for @KafkaListener methods.
     *
     * @return listener factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    /**
     * Topic carrying new order commands to inventory.
     *
     * @return order.created topic definition
     */
    @Bean
    public NewTopic orderCreatedTopic() {
        return new NewTopic("order.created", 1, (short) 1);
    }

    /**
     * Topic carrying order confirmations.
     *
     * @return order.confirmed topic
     */
    @Bean
    public NewTopic orderConfirmedTopic() {
        return new NewTopic("order.confirmed", 1, (short) 1);
    }

    /**
     * Topic carrying order cancellations.
     *
     * @return order.cancelled topic
     */
    @Bean
    public NewTopic orderCancelledTopic() {
        return new NewTopic("order.cancelled", 1, (short) 1);
    }
}
