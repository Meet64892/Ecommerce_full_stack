package com.smartshop.notification.kafka;

import com.smartshop.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * UserEventConsumer - Consumes user registration events.
 *
 * <h2>Purpose</h2>
 * Sends welcome messages when user accounts are created.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Event-driven side effects keep user-service write path fast.</li>
 *   <li>At-least-once delivery implies handlers should be idempotent.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Listens on user.registered and delegates delivery to NotificationService.
 *
 * @see com.smartshop.notification.service.EmailNotificationService
 * @author SmartShop Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final NotificationService notificationService;

    /**
     * Handles user registered events.
     *
     * @param event user registration payload
     */
    @KafkaListener(topics = "user.registered", groupId = "notification-service-group")
    public void onUserRegistered(final UserRegisteredEvent event) {
        notificationService.sendWelcomeEmail(event.email(), event.firstName());
        log.info("Processed user.registered for user {}", event.userId());
    }
}
