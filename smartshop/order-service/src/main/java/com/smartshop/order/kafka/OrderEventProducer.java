package com.smartshop.order.kafka;

import com.smartshop.order.event.OrderCancelledEvent;
import com.smartshop.order.event.OrderConfirmedEvent;
import com.smartshop.order.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * OrderEventProducer - Kafka publisher for order domain events.
 *
 * <h2>Purpose</h2>
 * Encapsulates topic names and event publication so saga logic remains business-focused.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Topic routing: semantic topics isolate event streams by business meaning.</li>
 *   <li>Fire-and-forget decoupling: producer does not block on consumer processing.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Called by saga orchestrator to emit lifecycle events.
 *
 * @see com.smartshop.order.service.SagaOrchestrator
 * @author SmartShop Team
 */
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publishes order created event.
     *
     * @param event created event payload
     */
    public void publishOrderCreated(final OrderCreatedEvent event) {
        kafkaTemplate.send("order.created", String.valueOf(event.getOrderId()), event);
    }

    /**
     * Publishes order confirmed event.
     *
     * @param event confirmed event payload
     */
    public void publishOrderConfirmed(final OrderConfirmedEvent event) {
        kafkaTemplate.send("order.confirmed", String.valueOf(event.getOrderId()), event);
    }

    /**
     * Publishes order cancelled event.
     *
     * @param event cancelled event payload
     */
    public void publishOrderCancelled(final OrderCancelledEvent event) {
        kafkaTemplate.send("order.cancelled", String.valueOf(event.getOrderId()), event);
    }
}
