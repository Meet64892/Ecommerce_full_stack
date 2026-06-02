package com.smartshop.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * NotificationServiceApplication - Async Notification Delivery Service
 *
 * <h2>Purpose</h2>
 * Consumes Kafka events from order-service and user-service, then sends
 * email notifications to customers. No database, no REST API — purely an
 * event-driven service that bridges Kafka events to email delivery.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Why Kafka instead of synchronous HTTP for notifications?
 *       If order-service called notification-service via HTTP:
 *       - Order placement would fail if notification-service is down
 *       - Order placement would be slower (waits for email to send)
 *       - Tight coupling: order-service needs to know notification-service's URL
 *       With Kafka:
 *       - Fire-and-forget: order-service publishes and doesn't wait
 *       - Resilience: notification-service processes when available
 *       - Decoupling: order-service doesn't know/care who sends emails</li>
 *   <li>Dead Letter Topics: If email sending fails (SMTP down), the message
 *       goes to the DLT after retries. Operations team can replay DLT messages
 *       once the email server is back up — no notifications lost permanently.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
