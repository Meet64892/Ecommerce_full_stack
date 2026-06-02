package com.smartshop.order.event;

import com.smartshop.common.event.BaseEvent;
import com.smartshop.order.dto.OrderItemDto;

import java.util.List;
import java.util.UUID;

/**
 * OrderCreatedEvent - Kafka event emitted when checkout starts.
 *
 * <h2>Purpose</h2>
 * The event asks inventory-service to reserve stock without blocking the HTTP request on a synchronous service call.
 * Kafka provides at-least-once delivery, so consumers must be idempotent and tolerate duplicate event ids.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Topic: `order.created` stores a sequence of order-created facts.</li>
 *   <li>Partition: Kafka uses partitions for ordering and parallelism within a topic.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * SagaOrchestrator publishes this event and inventory-service consumes it to reserve stock.
 *
 * @see com.smartshop.order.kafka.OrderEventProducer
 * @author SmartShop Team
 */
public class OrderCreatedEvent extends BaseEvent {
    private UUID orderId;
    private UUID userId;
    private List<OrderItemDto> items;

    /** Required by Jackson for Kafka deserialization. */
    public OrderCreatedEvent() { super("order.created"); }

    /**
     * Creates an order-created event.
     *
     * @param orderId order id
     * @param userId buyer id
     * @param items items requiring stock reservation
     */
    public OrderCreatedEvent(UUID orderId, UUID userId, List<OrderItemDto> items) {
        super("order.created");
        this.orderId = orderId;
        this.userId = userId;
        this.items = items;
    }
    /** @return order id */ public UUID getOrderId() { return orderId; }
    /** @param orderId order id */ public void setOrderId(UUID orderId) { this.orderId = orderId; }
    /** @return buyer id */ public UUID getUserId() { return userId; }
    /** @param userId buyer id */ public void setUserId(UUID userId) { this.userId = userId; }
    /** @return line items */ public List<OrderItemDto> getItems() { return items; }
    /** @param items line items */ public void setItems(List<OrderItemDto> items) { this.items = items; }
}
