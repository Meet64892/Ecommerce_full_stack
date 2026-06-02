package com.smartshop.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * NotificationServiceApplication - Async notification service bootstrap.
 *
 * <h2>Purpose</h2>
 * Notification is isolated to honor single-responsibility and prevent synchronous user-facing
 * APIs from waiting on email/SMS network calls.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Async decoupling: Kafka fire-and-forget avoids brittle direct HTTP chains.</li>
 *   <li>Consumer groups: each service group receives full topic stream independently.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Consumes user/order events and sends templated notifications.
 *
 * @see com.smartshop.notification.kafka.OrderEventConsumer
 * @author SmartShop Team
 */
@SpringBootApplication
public class NotificationServiceApplication {

    /**
     * Starts notification service.
     *
     * @param args startup args
     * @return nothing
     */
    public static void main(final String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
