package com.smartshop.notification.kafka;

import java.util.UUID;

/**
 * OrderConfirmedEvent - Local notification view of an order confirmation event.
 *
 * <h2>Purpose</h2>
 * Notification-service needs only order and user identifiers to send a confirmation message. Keeping this DTO local
 * avoids compile-time coupling to order-service internals while honoring the Kafka JSON contract.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Event contract: Field names match the producer payload.</li>
 *   <li>Loose coupling: Services share messages rather than direct code dependencies.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * OrderEventConsumer parses this DTO from the `order.confirmed` topic.
 *
 * @see OrderEventConsumer
 * @author SmartShop Team
 */
public class OrderConfirmedEvent {
    private UUID orderId;
    private UUID userId;
    /** Required by Jackson. */ public OrderConfirmedEvent() {}
    /** @return order id */ public UUID getOrderId() { return orderId; }
    /** @param orderId order id */ public void setOrderId(UUID orderId) { this.orderId = orderId; }
    /** @return user id */ public UUID getUserId() { return userId; }
    /** @param userId user id */ public void setUserId(UUID userId) { this.userId = userId; }
}
