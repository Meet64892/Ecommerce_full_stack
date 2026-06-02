package com.smartshop.notification.event;

import com.smartshop.common.event.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * OrderCancelledEvent - Notification-side view of order.cancelled.
 *
 * <h2>Purpose</h2>
 * Mirrors order-service's cancellation event for JSON consumption here.
 *
 * <h2>How it fits in the system</h2>
 * Consumed by {@code OrderEventConsumer} to send a cancellation email.
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

    /** Why it was cancelled (shown in the email). */
    private String reason;
}
