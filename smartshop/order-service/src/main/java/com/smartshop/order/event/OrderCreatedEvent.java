package com.smartshop.order.event;

import com.smartshop.common.event.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * OrderCreatedEvent - Published when a new order needs inventory reservation.
 *
 * <h2>Purpose</h2>
 * Kicks off the saga: inventory-service consumes it, attempts to reserve stock,
 * and replies with an {@link InventoryCheckedEvent}.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Extends {@link BaseEvent} so it carries eventId/eventType/timestamp for
 *       idempotency and tracing.</li>
 *   <li>Plain POJO with getters/setters so Kafka's JSON (de)serializer can map
 *       it both ways.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Produced by {@code OrderEventProducer} to the {@code order.created} topic.
 *
 * @author SmartShop Team
 */
@Getter
@Setter
@NoArgsConstructor
public class OrderCreatedEvent extends BaseEvent {

    /** The order this event concerns. */
    private Long orderId;

    /** The customer who placed it. */
    private Long userId;

    /** Map of productId -> quantity the inventory service must reserve. */
    private Map<Long, Integer> productQuantities;

    /**
     * @param orderId           the new order id
     * @param userId            owning customer
     * @param productQuantities productId -> quantity to reserve
     */
    public OrderCreatedEvent(Long orderId, Long userId, Map<Long, Integer> productQuantities) {
        super("ORDER_CREATED");
        this.orderId = orderId;
        this.userId = userId;
        this.productQuantities = productQuantities;
    }
}
