package com.smartshop.order.event;

import com.smartshop.common.event.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * OrderCancelledEvent - Emitted when an order is cancelled (saga compensation).
 *
 * <h2>Purpose</h2>
 * Notifies consumers that an order failed (e.g. out of stock) so they can inform
 * the customer and any other party can release held resources.
 *
 * <h2>How it fits in the system</h2>
 * Produced to {@code order.cancelled}; consumed by notification-service.
 *
 * @author SmartShop Team
 */
@Getter
@Setter
@NoArgsConstructor
public class OrderCancelledEvent extends BaseEvent {

    /** The cancelled order. */
    private Long orderId;

    /** The customer to notify. */
    private Long userId;

    /** Why the order was cancelled. */
    private String reason;

    /**
     * @param orderId cancelled order id
     * @param userId  owning customer
     * @param reason  cancellation reason
     */
    public OrderCancelledEvent(Long orderId, Long userId, String reason) {
        super("ORDER_CANCELLED");
        this.orderId = orderId;
        this.userId = userId;
        this.reason = reason;
    }
}
