package com.smartshop.order.kafka;

import com.smartshop.common.event.BaseEvent;
import com.smartshop.common.event.order.OrderCancelledEvent;
import com.smartshop.common.event.order.OrderConfirmedEvent;
import com.smartshop.common.event.order.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * OrderEventProducer - Kafka Event Publisher for Order Service
 *
 * <h2>Purpose</h2>
 * Publishes domain events to Kafka topics when order state changes occur.
 * Separating Kafka logic from business logic (OrderServiceImpl) follows
 * the Single Responsibility Principle.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>KafkaTemplate: Spring's abstraction over Kafka's producer API.
 *       send(topic, key, value) serializes the value to JSON and publishes to Kafka.
 *       The key determines which PARTITION the message goes to:
 *       Messages with the same key always go to the same partition,
 *       ensuring ORDER-PRESERVING delivery for a given key.
 *       We use orderId.toString() as the key so all events for one order
 *       are delivered in order to consumers.</li>
 *   <li>CompletableFuture: KafkaTemplate.send() is asynchronous.
 *       The future completes when the broker acknowledges the message.
 *       We add a callback to log success/failure without blocking the caller.</li>
 *   <li>Dead Letter Topic (DLT): When a consumer fails to process a message after
 *       all retries, Kafka sends it to a Dead Letter Topic (e.g., "order.created.DLT").
 *       Operations team monitors DLTs for messages that need manual investigation.
 *       Configure via @RetryableTopic in consumer or via application.yml.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    // Topic names as constants prevent typos and enable refactoring
    public static final String ORDER_CREATED_TOPIC = "order.created";
    public static final String ORDER_CONFIRMED_TOPIC = "order.confirmed";
    public static final String ORDER_CANCELLED_TOPIC = "order.cancelled";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publishes an OrderCreatedEvent to the "order.created" Kafka topic.
     * inventory-service and notification-service consume this event.
     *
     * @param event the order created event to publish
     */
    public void publishOrderCreated(OrderCreatedEvent event) {
        publishEvent(ORDER_CREATED_TOPIC, String.valueOf(event.getOrderId()), event);
    }

    /**
     * Publishes an OrderConfirmedEvent to the "order.confirmed" Kafka topic.
     * notification-service consumes this to send confirmation email.
     *
     * @param event the order confirmed event to publish
     */
    public void publishOrderConfirmed(OrderConfirmedEvent event) {
        publishEvent(ORDER_CONFIRMED_TOPIC, String.valueOf(event.getOrderId()), event);
    }

    /**
     * Publishes an OrderCancelledEvent to the "order.cancelled" Kafka topic.
     * inventory-service consumes this to release reserved stock (compensating transaction).
     * notification-service consumes this to send cancellation email.
     *
     * @param event the order cancelled event to publish
     */
    public void publishOrderCancelled(OrderCancelledEvent event) {
        publishEvent(ORDER_CANCELLED_TOPIC, String.valueOf(event.getOrderId()), event);
    }

    /**
     * Generic event publishing with async success/failure callbacks.
     * All publish methods delegate here for consistent error handling.
     *
     * @param topic   the Kafka topic to publish to
     * @param key     the partition key (orderId ensures ordering per order)
     * @param event   the event to publish (serialized to JSON by KafkaTemplate)
     */
    private void publishEvent(String topic, String key, BaseEvent event) {
        log.info("Publishing {} event to topic={} key={} correlationId={}",
                event.getEventType(), topic, key, event.getCorrelationId());

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);

        // Async callback: runs when the broker acknowledges the message
        future.whenComplete((result, throwable) -> {
            if (throwable != null) {
                // Failed to publish — log the error
                // In production: retry, store in an outbox table, or alert ops team
                log.error("Failed to publish {} event to topic={}: {}",
                        event.getEventType(), topic, throwable.getMessage(), throwable);
            } else {
                // Success: log partition and offset for debugging
                log.debug("Published {} event to topic={} partition={} offset={}",
                        event.getEventType(), topic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
