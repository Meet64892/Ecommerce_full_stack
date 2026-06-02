package com.smartshop.order.config;

import com.smartshop.common.event.order.InventoryCheckedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * KafkaConfig - Kafka Producer and Consumer Configuration
 *
 * <h2>Purpose</h2>
 * Configures all Kafka infrastructure: topics, producer factory, consumer factory,
 * error handling, and dead letter topics.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Partitions: Each topic has N partitions. Messages with the same key go to
 *       the same partition. More partitions = more parallelism (more consumers can
 *       read simultaneously). Too many partitions = overhead (each partition has
 *       replication overhead).</li>
 *   <li>Replication Factor: How many brokers hold copies of each partition.
 *       Factor 1 (dev): no redundancy, fast. Factor 3 (prod): one broker can fail safely.</li>
 *   <li>Dead Letter Topic: When processing fails after max retries, the message is
 *       sent to a DLT (Dead Letter Topic). Operations team investigates and can
 *       replay messages from the DLT after fixing the root cause.</li>
 *   <li>Error Handler: Configures retry behavior and dead letter routing.
 *       FixedBackOff(1000ms, 3 attempts): retry 3 times with 1 second delay between attempts.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Configuration
@EnableKafka
@Slf4j
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    // ==========================================================================
    // TOPIC DEFINITIONS
    // Topics are created automatically if they don't exist (auto.create.topics.enable=true)
    // Explicit topic creation via @Bean gives us control over partitions and replication.
    // ==========================================================================

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name("order.created")
                .partitions(3)      // 3 partitions for parallel consumption by 3 inventory-service instances
                .replicas(1)        // 1 replica for development (use 3 in production)
                .build();
    }

    @Bean
    public NewTopic orderConfirmedTopic() {
        return TopicBuilder.name("order.confirmed").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic orderCancelledTopic() {
        return TopicBuilder.name("order.cancelled").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic inventoryCheckedTopic() {
        return TopicBuilder.name("inventory.checked").partitions(3).replicas(1).build();
    }

    // ==========================================================================
    // PRODUCER CONFIGURATION
    // ==========================================================================

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // acks=all: wait for all in-sync replicas to acknowledge
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        // enable.idempotence=true: each message is delivered exactly once to the broker
        // (Kafka assigns a sequence number to each message from this producer)
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // ==========================================================================
    // CONSUMER CONFIGURATION
    // ==========================================================================

    @Bean
    public ConsumerFactory<String, InventoryCheckedEvent> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "order-service-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        // earliest: start from the beginning of the topic on first run
        // (ensures no messages are missed if the service was down when they were published)
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // Trusted packages: only deserialize our own event classes (security measure)
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.smartshop.*");
        return new DefaultKafkaConsumerFactory<>(config,
                new StringDeserializer(),
                new JsonDeserializer<>(InventoryCheckedEvent.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryCheckedEvent>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InventoryCheckedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        // Manual offset acknowledgment: commit offset AFTER successful processing
        // (not auto-commit which might commit before processing completes)
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        // Error handler: retry 3 times with 1-second intervals, then send to DLT
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(kafkaTemplate()),
                new FixedBackOff(1000L, 3L)  // 1s interval, 3 retries
        );
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
}
