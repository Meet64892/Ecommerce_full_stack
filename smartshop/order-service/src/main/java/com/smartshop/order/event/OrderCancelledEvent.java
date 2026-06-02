package com.smartshop.order.event;

import com.smartshop.common.event.BaseEvent;

import java.util.UUID;

/**
 * OrderCancelledEvent - Kafka event emitted when a Saga cannot complete.
 *
 * <h2>Purpose</h2>
 * Cancellation is a business fact that notifications, analytics, and support tooling may need. Publishing it through
 * Kafka decouples those consumers from order-service internals.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Compensation: A Saga uses follow-up actions instead of rolling back remote databases directly.</li>
 *   <li>Event stream: The cancellation remains available for replay by new consumers.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * SagaOrchestrator publishes this after marking an order CANCELLED.
 *
 * @see OrderConfirmedEvent
 * @author SmartShop Team
 */
public class OrderCancelledEvent extends BaseEvent {
    private UUID orderId;
    private UUID userId;
    private String reason;
    /** Required by Jackson. */ public OrderCancelledEvent() { super("order.cancelled"); }
    /** @param orderId order id @param userId buyer id @param reason cancellation reason */ public OrderCancelledEvent(UUID orderId, UUID userId, String reason) { super("order.cancelled"); this.orderId = orderId; this.userId = userId; this.reason = reason; }
    /** @return order id */ public UUID getOrderId() { return orderId; }
    /** @param orderId order id */ public void setOrderId(UUID orderId) { this.orderId = orderId; }
    /** @return buyer id */ public UUID getUserId() { return userId; }
    /** @param userId buyer id */ public void setUserId(UUID userId) { this.userId = userId; }
    /** @return cancellation reason */ public String getReason() { return reason; }
    /** @param reason cancellation reason */ public void setReason(String reason) { this.reason = reason; }
}
