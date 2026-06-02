package com.smartshop.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * NotificationServiceApplication - Bootstraps the notification service.
 *
 * <h2>Purpose</h2>
 * Reacts to platform events by sending notifications (emails). It is a dedicated
 * service so that messaging concerns evolve and scale independently of the core
 * business services.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Single Responsibility + async decoupling</b>: order/user services
 *       don't block on (or know about) email delivery — they just emit events.
 *       This service owns the "how to notify" logic.</li>
 *   <li><b>Why Kafka over direct HTTP</b>: notifications are fire-and-forget. If
 *       this service is briefly down, events wait durably in Kafka and are
 *       processed on recovery — no lost emails, no caller blocked/retrying.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Consumes {@code order.confirmed}, {@code order.cancelled}, and
 * {@code user.registered}; renders + "sends" emails.
 *
 * @author SmartShop Team
 */
@SpringBootApplication
public class NotificationServiceApplication {

    /**
     * Spring Boot entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
