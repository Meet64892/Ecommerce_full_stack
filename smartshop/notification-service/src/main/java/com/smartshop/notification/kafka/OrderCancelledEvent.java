package com.smartshop.notification.kafka;

import java.util.UUID;

/**
 * OrderCancelledEvent - Local notification view of an order cancellation event.
 *
 * <h2>Purpose</h2>
 * Cancellation messages tell users when a Saga failed and why. The DTO includes a reason so templates can provide
 * useful context without calling order-service synchronously.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Compensation visibility: Users should know when a checkout step could not complete.</li>
 *   <li>Fire-and-forget: The event drives communication asynchronously.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * OrderEventConsumer parses this DTO from the `order.cancelled` topic.
 *
 * @see OrderEventConsumer
 * @author SmartShop Team
 */
public class OrderCancelledEvent {
    private UUID orderId;
    private UUID userId;
    private String reason;
    /** Required by Jackson. */ public OrderCancelledEvent() {}
    /** @return order id */ public UUID getOrderId() { return orderId; }
    /** @param orderId order id */ public void setOrderId(UUID orderId) { this.orderId = orderId; }
    /** @return user id */ public UUID getUserId() { return userId; }
    /** @param userId user id */ public void setUserId(UUID userId) { this.userId = userId; }
    /** @return cancellation reason */ public String getReason() { return reason; }
    /** @param reason cancellation reason */ public void setReason(String reason) { this.reason = reason; }
}
