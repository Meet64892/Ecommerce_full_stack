package com.smartshop.notification.service;

import java.util.UUID;

/**
 * NotificationService - Application contract for outbound user communications.
 *
 * <h2>Purpose</h2>
 * The interface hides whether messages are sent through email, SMS, or mocked logging. This makes Kafka consumers
 * independent from delivery infrastructure.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Single responsibility: Notification delivery logic lives outside order and user services.</li>
 *   <li>Dependency inversion: Consumers depend on an interface instead of JavaMail directly.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Kafka consumers call this interface and EmailNotificationService implements template rendering and delivery.
 *
 * @see EmailNotificationService
 * @author SmartShop Team
 */
public interface NotificationService {
    /**
     * Sends an order confirmation notification.
     *
     * @param orderId confirmed order id
     * @param userId buyer id
     */
    void sendOrderConfirmed(UUID orderId, UUID userId);

    /**
     * Sends an order cancellation notification.
     *
     * @param orderId cancelled order id
     * @param userId buyer id
     * @param reason cancellation reason
     */
    void sendOrderCancelled(UUID orderId, UUID userId, String reason);

    /**
     * Sends a welcome notification.
     *
     * @param email recipient email
     * @param firstName recipient first name
     */
    void sendWelcome(String email, String firstName);
}
