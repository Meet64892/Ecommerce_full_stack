package com.smartshop.inventory.kafka;

import com.smartshop.common.event.BaseEvent;

import java.util.List;
import java.util.UUID;

/**
 * OrderCreatedEvent - Local copy of the order-created Kafka contract.
 *
 * <h2>Purpose</h2>
 * Event contracts are integration boundaries. Inventory-service keeps a local representation so it can evolve its code
 * independently while still honoring the JSON schema published on `order.created`.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Contract compatibility: Field names must match the producer payload.</li>
 *   <li>Idempotency key: BaseEvent.eventId can be stored to ignore duplicates in a production system.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * OrderEventConsumer receives this event and reserves each requested product quantity.
 *
 * @see OrderEventConsumer
 * @author SmartShop Team
 */
public class OrderCreatedEvent extends BaseEvent {
    private UUID orderId;
    private UUID userId;
    private List<OrderItemMessage> items;
    /** Required by Jackson. */ public OrderCreatedEvent() { super("order.created"); }
    /** @return order id */ public UUID getOrderId() { return orderId; }
    /** @param orderId order id */ public void setOrderId(UUID orderId) { this.orderId = orderId; }
    /** @return buyer id */ public UUID getUserId() { return userId; }
    /** @param userId buyer id */ public void setUserId(UUID userId) { this.userId = userId; }
    /** @return order items */ public List<OrderItemMessage> getItems() { return items; }
    /** @param items order items */ public void setItems(List<OrderItemMessage> items) { this.items = items; }
}
