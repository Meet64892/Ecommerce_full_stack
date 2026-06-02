package com.smartshop.user.event;

/**
 * UserRegisteredEvent - Event emitted when a user account is created.
 *
 * <h2>Purpose</h2>
 * This event decouples welcome-notification workflows from synchronous registration response time.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Async integration: notification service can consume without tight coupling.</li>
 *   <li>At-least-once handling: consumers should remain idempotent on retries.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Published by UserService and consumed by notification-service user consumer.
 *
 * @see com.smartshop.user.service.UserServiceImpl
 * @author SmartShop Team
 */
public record UserRegisteredEvent(Long userId, String email, String firstName) {
}
