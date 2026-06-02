package com.smartshop.notification.kafka;

import com.smartshop.common.util.JsonUtils;
import com.smartshop.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * UserEventConsumer - Consumes user registration events for welcome messages.
 *
 * <h2>Purpose</h2>
 * Welcome emails are useful but not part of the critical registration transaction. Consuming `user.registered` lets
 * user-service remain fast and available even if email delivery is delayed.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Async side effect: Email is triggered after the user account exists.</li>
 *   <li>Independent consumer group: Notification-service sees each registration event once per group.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * The consumer parses registration events and calls NotificationService.sendWelcome.
 *
 * @see NotificationService
 * @author SmartShop Team
 */
@Slf4j
@Component
public class UserEventConsumer {
    private final NotificationService notificationService;

    /**
     * Creates the consumer with the notification service.
     *
     * @param notificationService notification delivery abstraction
     */
    public UserEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Handles user registration events.
     *
     * @param payload JSON payload from Kafka
     */
    @KafkaListener(topics = "user.registered", groupId = "notification-service")
    public void onUserRegistered(String payload) {
        UserRegisteredEvent event = JsonUtils.fromJson(payload, UserRegisteredEvent.class);
        log.info("Sending welcome email to {}", event.getEmail());
        notificationService.sendWelcome(event.getEmail(), event.getFirstName());
    }
}
