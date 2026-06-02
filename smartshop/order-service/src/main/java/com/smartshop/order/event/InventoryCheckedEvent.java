package com.smartshop.order.event;

import com.smartshop.common.event.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * InventoryCheckedEvent - The inventory-service's reply to an order request.
 *
 * <h2>Purpose</h2>
 * Tells the saga whether stock was successfully reserved, so it can either
 * confirm the order or cancel it (compensation).
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>This is the inbound half of a request/reply over Kafka: order publishes
 *       OrderCreated, inventory publishes InventoryChecked.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Produced by inventory-service to {@code inventory.checked}; consumed by
 * {@code InventoryResponseConsumer} here.
 *
 * @author SmartShop Team
 */
@Getter
@Setter
@NoArgsConstructor
public class InventoryCheckedEvent extends BaseEvent {

    /** The order the check refers to. */
    private Long orderId;

    /** True if all requested stock was reserved. */
    private boolean reserved;

    /** Human-readable reason when reservation failed. */
    private String reason;

    /**
     * @param orderId  the order id
     * @param reserved whether stock was reserved
     * @param reason   failure reason (null on success)
     */
    public InventoryCheckedEvent(Long orderId, boolean reserved, String reason) {
        super("INVENTORY_CHECKED");
        this.orderId = orderId;
        this.reserved = reserved;
        this.reason = reason;
    }
}
