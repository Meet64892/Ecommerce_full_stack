package com.smartshop.order.event;

import com.smartshop.common.event.BaseEvent;

import java.util.UUID;

/**
 * OrderConfirmedEvent - Kafka event emitted after inventory reservation succeeds.
 *
 * <h2>Purpose</h2>
 * Notification-service and other downstream consumers need to know when an order is confirmed. Publishing an event
 * avoids direct HTTP coupling and lets each consumer process independently.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Consumer group: Each group receives its own copy of the topic stream.</li>
 *   <li>At-least-once delivery: Consumers should use eventId/orderId to avoid duplicate side effects.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * SagaOrchestrator publishes this after setting the order status to CONFIRMED.
 *
 * @see OrderCancelledEvent
 * @author SmartShop Team
 */
public class OrderConfirmedEvent extends BaseEvent {
    private UUID orderId;
    private UUID userId;
    /** Required by Jackson. */ public OrderConfirmedEvent() { super("order.confirmed"); }
    /** @param orderId order id @param userId buyer id */ public OrderConfirmedEvent(UUID orderId, UUID userId) { super("order.confirmed"); this.orderId = orderId; this.userId = userId; }
    /** @return order id */ public UUID getOrderId() { return orderId; }
    /** @param orderId order id */ public void setOrderId(UUID orderId) { this.orderId = orderId; }
    /** @return buyer id */ public UUID getUserId() { return userId; }
    /** @param userId buyer id */ public void setUserId(UUID userId) { this.userId = userId; }
}
