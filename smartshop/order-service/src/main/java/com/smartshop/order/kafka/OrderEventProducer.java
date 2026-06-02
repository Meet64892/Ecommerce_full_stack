package com.smartshop.order.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * OrderEventProducer - Publishes order Saga events to Kafka.
 *
 * <h2>Purpose</h2>
 * Kafka decouples order-service from inventory and notification services. Producers append facts to topics and
 * consumers process them at their own pace, which is safer than chaining synchronous HTTP calls through checkout.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Topic: Named log of records such as `order.created`.</li>
 *   <li>At-least-once delivery: A successful send can still be observed more than once by consumers after retries.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * SagaOrchestrator calls this producer after local order state changes.
 *
 * @see com.smartshop.order.service.SagaOrchestrator
 * @author SmartShop Team
 */
@Slf4j
@Component
public class OrderEventProducer {
    public static final String ORDER_CREATED = "order.created";
    public static final String ORDER_CONFIRMED = "order.confirmed";
    public static final String ORDER_CANCELLED = "order.cancelled";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Creates the producer with a KafkaTemplate.
     *
     * @param kafkaTemplate Spring Kafka helper for sending records
     */
    public OrderEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes an event to a topic with a string key.
     *
     * @param topic destination Kafka topic
     * @param key partitioning key, usually order id
     * @param event event payload serialized as JSON
     */
    public void publish(String topic, String key, Object event) {
        kafkaTemplate.send(topic, key, event).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish {} with key {}", topic, key, ex);
            } else {
                log.info("Published {} with key {}", topic, key);
            }
        });
    }
}
