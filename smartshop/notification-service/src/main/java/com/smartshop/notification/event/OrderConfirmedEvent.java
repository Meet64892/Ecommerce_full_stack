package com.smartshop.notification.event;

import com.smartshop.common.event.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * OrderConfirmedEvent - Notification-side view of order.confirmed.
 *
 * <h2>Purpose</h2>
 * Structurally mirrors order-service's event so the JSON deserializes here,
 * keeping the services decoupled (no shared event jar).
 *
 * <h2>How it fits in the system</h2>
 * Consumed by {@code OrderEventConsumer} to send a confirmation email.
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
}
