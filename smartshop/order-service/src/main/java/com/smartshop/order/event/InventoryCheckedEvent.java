package com.smartshop.order.event;

import com.smartshop.common.event.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * InventoryCheckedEvent - Response from inventory reservation step.
 *
 * <h2>Purpose</h2>
 * Communicates whether stock reservation succeeded so saga can continue or compensate.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Saga orchestration: coordinator waits for this event before next transition.</li>
 *   <li>Idempotency: repeated responses must not re-apply transitions unsafely.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Consumed by order-service from inventory.checked Kafka topic.
 *
 * @see com.smartshop.order.kafka.InventoryResponseConsumer
 * @author SmartShop Team
 */
@Getter
@NoArgsConstructor
public class InventoryCheckedEvent extends BaseEvent {

    private Long orderId;
    private boolean available;
    private String reason;

    /**
     * Creates inventory response event.
     *
     * @param orderId order id
     * @param available stock availability result
     * @param reason optional failure reason
     */
    public InventoryCheckedEvent(final Long orderId, final boolean available, final String reason) {
        super("INVENTORY_CHECKED");
        this.orderId = orderId;
        this.available = available;
        this.reason = reason;
    }
}
