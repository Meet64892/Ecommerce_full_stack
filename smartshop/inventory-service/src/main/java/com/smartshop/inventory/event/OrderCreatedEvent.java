package com.smartshop.inventory.event;

import com.smartshop.common.event.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * OrderCreatedEvent - Inventory-side view of the order.created event.
 *
 * <h2>Purpose</h2>
 * Kafka events are exchanged as JSON, so each service keeps a structurally
 * compatible copy of the payload it consumes. This mirrors order-service's
 * OrderCreatedEvent fields so JSON maps cleanly here without sharing code.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Schema-by-structure</b>: matching field names + types is all the JSON
 *       (de)serializer needs; services stay loosely coupled (no shared event jar
 *       required, avoiding lockstep deploys).</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Deserialized by {@code OrderEventConsumer} from the {@code order.created} topic.
 *
 * @author SmartShop Team
 */
@Getter
@Setter
@NoArgsConstructor
public class OrderCreatedEvent extends BaseEvent {

    /** The order requiring reservation. */
    private Long orderId;

    /** Owning customer. */
    private Long userId;

    /** productId -> quantity to reserve. */
    private Map<Long, Integer> productQuantities;
}
