package com.smartshop.notification.kafka;

/**
 * OrderConfirmedEvent - Notification-facing view of order confirmation event.
 *
 * <h2>Purpose</h2>
 * Carries minimum data needed to notify users after successful order confirmation.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Contract projection keeps consumer decoupled from producer internals.</li>
 *   <li>Topic-based integration supports independent deployability.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Consumed by OrderEventConsumer from order.confirmed topic.
 *
 * @see OrderEventConsumer
 * @author SmartShop Team
 */
public record OrderConfirmedEvent(Long orderId) {
}
