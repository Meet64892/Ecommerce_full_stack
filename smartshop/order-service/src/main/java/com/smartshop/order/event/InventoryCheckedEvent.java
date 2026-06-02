package com.smartshop.order.event;

import com.smartshop.common.event.BaseEvent;

import java.util.UUID;

/**
 * InventoryCheckedEvent - Kafka reply indicating stock reservation result.
 *
 * <h2>Purpose</h2>
 * Inventory-service emits this event after attempting reservation. The order Saga uses it to either confirm the order
 * or cancel it with a reason.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Offset: Kafka tracks consumer progress so order-service can resume after restart.</li>
 *   <li>Idempotency: Reprocessing the same reply should not corrupt order state.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * InventoryResponseConsumer receives this event and delegates to SagaOrchestrator.
 *
 * @see com.smartshop.order.kafka.InventoryResponseConsumer
 * @author SmartShop Team
 */
public class InventoryCheckedEvent extends BaseEvent {
    private UUID orderId;
    private boolean reserved;
    private String reason;
    /** Required by Jackson. */ public InventoryCheckedEvent() { super("inventory.checked"); }
    /** @param orderId order id @param reserved reservation result @param reason failure reason or success detail */ public InventoryCheckedEvent(UUID orderId, boolean reserved, String reason) { super("inventory.checked"); this.orderId = orderId; this.reserved = reserved; this.reason = reason; }
    /** @return order id */ public UUID getOrderId() { return orderId; }
    /** @param orderId order id */ public void setOrderId(UUID orderId) { this.orderId = orderId; }
    /** @return true when stock was reserved */ public boolean isReserved() { return reserved; }
    /** @param reserved reservation result */ public void setReserved(boolean reserved) { this.reserved = reserved; }
    /** @return human-readable reason */ public String getReason() { return reason; }
    /** @param reason human-readable reason */ public void setReason(String reason) { this.reason = reason; }
}
