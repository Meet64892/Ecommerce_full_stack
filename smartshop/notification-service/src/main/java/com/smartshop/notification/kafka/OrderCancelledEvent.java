package com.smartshop.notification.kafka;

/**
 * OrderCancelledEvent - Notification payload for cancellations.
 *
 * <h2>Purpose</h2>
 * Provides cancellation context for user-facing communication.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Event-driven UX: users are informed without synchronous coupling.</li>
 *   <li>Failure transparency: reason field explains cancellation.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Consumed from order.cancelled topic.
 *
 * @see OrderEventConsumer
 * @author SmartShop Team
 */
public record OrderCancelledEvent(Long orderId, String reason) {
}
