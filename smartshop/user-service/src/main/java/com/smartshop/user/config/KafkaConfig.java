package com.smartshop.user.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * KafkaConfig - Configures user-service event publishing.
 *
 * <h2>Purpose</h2>
 * Registration events allow notification-service to send welcome emails asynchronously. Configuring Kafka in this
 * service keeps user-domain events reliable and consistent with order-domain event publishing.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Producer factory: Holds serializer and broker settings for KafkaTemplate.</li>
 *   <li>Topic creation: NewTopic declares local development topics automatically.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * UserEventProducer uses KafkaTemplate and notification-service consumes the declared topic.
 *
 * @see com.smartshop.user.kafka.UserEventProducer
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
     * Builds JSON producer settings.
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
     * Creates KafkaTemplate for user events.
     *
     * @return Kafka template
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Declares user.registered topic for local development.
     *
     * @return topic definition
     */
    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name("user.registered").partitions(3).replicas(1).build();
    }
}
