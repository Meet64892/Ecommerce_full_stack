package com.smartshop.order.kafka;

import com.smartshop.order.config.KafkaConfig;
import com.smartshop.order.event.OrderCancelledEvent;
import com.smartshop.order.event.OrderConfirmedEvent;
import com.smartshop.order.event.OrderCreatedEvent;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * OrderEventProducer - Publishes order lifecycle events to Kafka.
 *
 * <h2>Purpose</h2>
 * Centralizes all outbound event publishing so topic names and keying are
 * consistent and the rest of the code just calls a typed method.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Message key = orderId</b>: Kafka routes records with the same key to
 *       the same partition, preserving per-order ordering of events.</li>
 *   <li><b>@Retry</b>: wraps the send in the Resilience4j "kafkaPublish" retry
 *       so a transient broker error is retried with backoff instead of failing
 *       the whole request.</li>
 *   <li><b>at-least-once</b>: Kafka may deliver a message more than once on
 *       retry, which is WHY consumers must be idempotent (keyed on eventId).</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Invoked by {@code SagaOrchestrator}/{@code OrderServiceImpl} to drive the saga.
 *
 * @author SmartShop Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    /** Typed template: <key=String, value=Object> serialized as JSON. */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publishes a new-order event to start the saga.
     *
     * @param event the order-created event
     */
    @Retry(name = "kafkaPublish")
    public void publishOrderCreated(OrderCreatedEvent event) {
        log.info("Publishing ORDER_CREATED for order {} (eventId={})", event.getOrderId(), event.getEventId());
        // Key by orderId so all events for an order share a partition (ordering).
        kafkaTemplate.send(KafkaConfig.ORDER_CREATED, String.valueOf(event.getOrderId()), event);
    }

    /**
     * Publishes a confirmation event.
     *
     * @param event the order-confirmed event
     */
    @Retry(name = "kafkaPublish")
    public void publishOrderConfirmed(OrderConfirmedEvent event) {
        log.info("Publishing ORDER_CONFIRMED for order {}", event.getOrderId());
        kafkaTemplate.send(KafkaConfig.ORDER_CONFIRMED, String.valueOf(event.getOrderId()), event);
    }

    /**
     * Publishes a cancellation event (compensation).
     *
     * @param event the order-cancelled event
     */
    @Retry(name = "kafkaPublish")
    public void publishOrderCancelled(OrderCancelledEvent event) {
        log.info("Publishing ORDER_CANCELLED for order {} reason={}", event.getOrderId(), event.getReason());
        kafkaTemplate.send(KafkaConfig.ORDER_CANCELLED, String.valueOf(event.getOrderId()), event);
    }
}
