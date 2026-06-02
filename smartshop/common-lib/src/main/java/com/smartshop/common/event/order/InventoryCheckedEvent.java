package com.smartshop.common.event.order;

import com.smartshop.common.event.BaseEvent;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * InventoryCheckedEvent - Kafka Reply from inventory-service
 *
 * <h2>Purpose</h2>
 * Published by inventory-service to the "inventory.checked" topic after
 * attempting to reserve stock for an order. The Saga orchestrator in
 * order-service consumes this to decide: confirm or cancel the order.
 *
 * @author SmartShop Team
 */
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryCheckedEvent extends BaseEvent {

    private Long orderId;

    /**
     * Whether inventory was successfully reserved for all order items.
     * true = all items reserved → proceed to payment
     * false = some items out of stock → cancel order
     */
    private boolean stockAvailable;

    /**
     * Human-readable reason when stockAvailable is false.
     * Example: "Product SKU-123 has only 2 units, 5 requested"
     */
    private String failureReason;
}
