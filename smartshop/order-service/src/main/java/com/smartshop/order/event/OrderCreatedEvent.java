package com.smartshop.order.event;

import com.smartshop.common.event.BaseEvent;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * OrderCreatedEvent - Emitted when a new order enters PENDING state.
 *
 * <h2>Purpose</h2>
 * Triggers downstream inventory reservation as the first saga step.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Event inheritance: extends BaseEvent for traceability metadata.</li>
 *   <li>At-least-once delivery: consumers must be idempotent.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Published by order-service and consumed by inventory-service.
 *
 * @see com.smartshop.order.kafka.OrderEventProducer
 * @author SmartShop Team
 */
@Getter
@NoArgsConstructor
public class OrderCreatedEvent extends BaseEvent {

    private Long orderId;
    private Long userId;
    private BigDecimal totalAmount;

    /**
     * Constructs order created event payload.
     *
     * @param orderId order identifier
     * @param userId user placing order
     * @param totalAmount order total
     */
    public OrderCreatedEvent(final Long orderId, final Long userId, final BigDecimal totalAmount) {
        super("ORDER_CREATED");
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
    }
}
