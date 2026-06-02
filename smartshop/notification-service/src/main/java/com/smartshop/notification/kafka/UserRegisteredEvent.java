package com.smartshop.notification.kafka;

/**
 * UserRegisteredEvent - Notification payload for new user onboarding.
 *
 * <h2>Purpose</h2>
 * Drives welcome-email workflow asynchronously after registration.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Asynchronous onboarding avoids slowing down registration response.</li>
 *   <li>Consumer retries and dead-letter topics increase reliability.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Consumed by UserEventConsumer from user.registered topic.
 *
 * @see UserEventConsumer
 * @author SmartShop Team
 */
public record UserRegisteredEvent(Long userId, String email, String firstName) {
}
