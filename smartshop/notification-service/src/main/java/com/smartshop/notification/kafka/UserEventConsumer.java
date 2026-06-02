package com.smartshop.notification.kafka;

import com.smartshop.notification.event.UserRegisteredEvent;
import com.smartshop.notification.service.NotificationService;
import com.smartshop.notification.template.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * UserEventConsumer - Sends a welcome email on new user registration.
 *
 * <h2>Purpose</h2>
 * Listens to {@code user.registered} and welcomes the new customer.
 *
 * <h2>How it fits in the system</h2>
 * Consumes user events; renders the "welcome" template and sends it.
 *
 * @author SmartShop Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final NotificationService notificationService;
    private final EmailTemplateService templateService;

    /**
     * Sends a welcome email to a newly registered user.
     *
     * @param event the user-registered event
     */
    @KafkaListener(topics = "user.registered", groupId = "notification-service")
    public void onUserRegistered(UserRegisteredEvent event) {
        log.info("Consumed USER_REGISTERED for user {} (eventId={})", event.getUserId(), event.getEventId());
        String body = templateService.render("welcome",
                Map.of("fullName", String.valueOf(event.getFullName())));
        notificationService.send(event.getEmail(), "Welcome to SmartShop!", body);
    }
}
