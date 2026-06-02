package com.smartshop.inventory.kafka;

import com.smartshop.common.event.BaseEvent;

import java.util.UUID;

/**
 * InventoryCheckedEvent - Kafka reply published after stock reservation.
 *
 * <h2>Purpose</h2>
 * Order-service needs an asynchronous answer after inventory attempts reservation. This event communicates success or
 * failure without requiring inventory-service to call order-service over HTTP.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Fire-and-forget: Kafka lets inventory publish and continue without waiting for notification consumers.</li>
 *   <li>Dead letter topic: Production consumers route poison messages away from the main stream after retries.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * OrderEventConsumer publishes this event to `inventory.checked` for order-service to consume.
 *
 * @see OrderEventConsumer
 * @author SmartShop Team
 */
public class InventoryCheckedEvent extends BaseEvent {
    private UUID orderId;
    private boolean reserved;
    private String reason;
    /** Required by Jackson. */ public InventoryCheckedEvent() { super("inventory.checked"); }
    /** @param orderId order id @param reserved result @param reason reason text */ public InventoryCheckedEvent(UUID orderId, boolean reserved, String reason) { super("inventory.checked"); this.orderId = orderId; this.reserved = reserved; this.reason = reason; }
    /** @return order id */ public UUID getOrderId() { return orderId; }
    /** @param orderId order id */ public void setOrderId(UUID orderId) { this.orderId = orderId; }
    /** @return true when reservation succeeded */ public boolean isReserved() { return reserved; }
    /** @param reserved reservation result */ public void setReserved(boolean reserved) { this.reserved = reserved; }
    /** @return reason */ public String getReason() { return reason; }
    /** @param reason reason */ public void setReason(String reason) { this.reason = reason; }
}
