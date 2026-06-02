package com.smartshop.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * NotificationServiceApplication - Starts asynchronous email/SMS notification handling.
 *
 * <h2>Purpose</h2>
 * Notifications are separated from order and user services because sending email is a side effect that should not slow
 * or fail core business transactions. This service follows single responsibility and processes events independently.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Async decoupling: Kafka lets producers publish and continue without waiting for email delivery.</li>
 *   <li>Consumer groups: Notification-service gets every relevant message independently of other consumers.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Order and user events arrive on Kafka topics, consumers render templates, and notification services send or mock email.
 *
 * @see com.smartshop.notification.kafka.OrderEventConsumer
 * @author SmartShop Team
 */
@SpringBootApplication
public class NotificationServiceApplication {
    /**
     * Starts the notification service.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
