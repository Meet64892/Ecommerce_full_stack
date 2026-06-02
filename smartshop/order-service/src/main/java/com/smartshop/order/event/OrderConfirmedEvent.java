package com.smartshop.order.event;

import com.smartshop.common.event.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * OrderConfirmedEvent - Emitted when an order is successfully confirmed.
 *
 * <h2>Purpose</h2>
 * Signals downstream consumers (notification-service) that the order is good,
 * so they can email the customer a confirmation.
 *
 * <h2>How it fits in the system</h2>
 * Produced to {@code order.confirmed}; consumed by notification-service.
 *
 * @author SmartShop Team
 */
@Getter
@Setter
@NoArgsConstructor
public class OrderConfirmedEvent extends BaseEvent {

    /** The confirmed order. */
    private Long orderId;

    /** The customer to notify. */
    private Long userId;

    /**
     * @param orderId confirmed order id
     * @param userId  owning customer
     */
    public OrderConfirmedEvent(Long orderId, Long userId) {
        super("ORDER_CONFIRMED");
        this.orderId = orderId;
        this.userId = userId;
    }
}
