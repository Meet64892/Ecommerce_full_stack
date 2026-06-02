package com.smartshop.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * KafkaConfig - Declares the Kafka topics this service owns/produces.
 *
 * <h2>Purpose</h2>
 * Declaring topics as beans lets Spring Kafka's admin auto-create them on
 * startup (in dev), so you don't have to pre-provision topics manually.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Topic</b>: a named, append-only log of messages.</li>
 *   <li><b>Partitions</b>: a topic is split into partitions for parallelism and
 *       ordering — Kafka guarantees order WITHIN a partition. We key messages by
 *       orderId so all events for one order land in the same partition and stay
 *       ordered.</li>
 *   <li><b>Consumer groups</b>: consumers in the same group share partitions
 *       (each partition goes to exactly one member) enabling horizontal scaling;
 *       different groups each receive ALL messages independently.</li>
 *   <li>Producer/consumer factories themselves are configured via properties in
 *       application.yml (bootstrap servers, (de)serializers, trusted packages).</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Topics created here are produced to by {@code OrderEventProducer} and consumed
 * across inventory- and notification-service.
 *
 * @author SmartShop Team
 */
@Configuration
public class KafkaConfig {

    /** Topic carrying new-order requests to inventory. */
    public static final String ORDER_CREATED = "order.created";
    /** Topic carrying confirmed orders to notification. */
    public static final String ORDER_CONFIRMED = "order.confirmed";
    /** Topic carrying cancelled orders to notification. */
    public static final String ORDER_CANCELLED = "order.cancelled";

    /**
     * @return the order.created topic (3 partitions for parallelism, 1 replica in dev)
     */
    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(ORDER_CREATED).partitions(3).replicas(1).build();
    }

    /**
     * @return the order.confirmed topic
     */
    @Bean
    public NewTopic orderConfirmedTopic() {
        return TopicBuilder.name(ORDER_CONFIRMED).partitions(3).replicas(1).build();
    }

    /**
     * @return the order.cancelled topic
     */
    @Bean
    public NewTopic orderCancelledTopic() {
        return TopicBuilder.name(ORDER_CANCELLED).partitions(3).replicas(1).build();
    }
}
