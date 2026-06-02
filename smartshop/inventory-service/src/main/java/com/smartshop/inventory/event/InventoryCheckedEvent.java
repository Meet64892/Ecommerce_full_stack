package com.smartshop.inventory.event;

import com.smartshop.common.event.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * InventoryCheckedEvent - The reply published after a reservation attempt.
 *
 * <h2>Purpose</h2>
 * Tells the order saga whether stock was reserved. Mirrors order-service's
 * InventoryCheckedEvent fields so the JSON the order consumer reads lines up.
 *
 * <h2>How it fits in the system</h2>
 * Produced by {@code OrderEventConsumer} to {@code inventory.checked}.
 *
 * @author SmartShop Team
 */
@Getter
@Setter
@NoArgsConstructor
public class InventoryCheckedEvent extends BaseEvent {

    /** The order the result refers to. */
    private Long orderId;

    /** Whether all requested stock was reserved. */
    private boolean reserved;

    /** Failure reason (null on success). */
    private String reason;

    /**
     * @param orderId  the order id
     * @param reserved reservation outcome
     * @param reason   failure reason or null
     */
    public InventoryCheckedEvent(Long orderId, boolean reserved, String reason) {
        super("INVENTORY_CHECKED");
        this.orderId = orderId;
        this.reserved = reserved;
        this.reason = reason;
    }
}
