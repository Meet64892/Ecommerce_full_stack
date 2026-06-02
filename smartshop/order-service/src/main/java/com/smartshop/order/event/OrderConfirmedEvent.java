package com.smartshop.order.event;

import com.smartshop.common.event.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * OrderConfirmedEvent - Emitted when saga confirms an order.
 *
 * <h2>Purpose</h2>
 * Notifies downstream services (notifications, fulfillment) that order is confirmed.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Async fan-out: multiple consumers can react independently via consumer groups.</li>
 *   <li>Event contract: stable payload supports decoupled service evolution.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Produced after inventory and payment stages succeed.
 *
 * @see OrderCancelledEvent
 * @author SmartShop Team
 */
@Getter
@NoArgsConstructor
public class OrderConfirmedEvent extends BaseEvent {

    private Long orderId;

    /**
     * Builds confirmed event.
     *
     * @param orderId confirmed order id
     */
    public OrderConfirmedEvent(final Long orderId) {
        super("ORDER_CONFIRMED");
        this.orderId = orderId;
    }
}
