package com.smartshop.notification.service;

/**
 * NotificationService - Notification delivery contract.
 *
 * <h2>Purpose</h2>
 * Abstracts transport details (email/sms/push) behind domain-oriented methods.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Port abstraction: consumers depend on intent, not channel implementation.</li>
 *   <li>Channel extensibility: easy to add SMS/push later.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Kafka consumers call this interface for notification side effects.
 *
 * @see EmailNotificationService
 * @author SmartShop Team
 */
public interface NotificationService {

    /**
     * Sends welcome message for newly registered user.
     *
     * @param email recipient email
     * @param firstName recipient first name
     */
    void sendWelcomeEmail(String email, String firstName);

    /**
     * Sends order-confirmed message.
     *
     * @param email recipient email
     * @param orderId order identifier
     */
    void sendOrderConfirmedEmail(String email, Long orderId);

    /**
     * Sends order-cancelled message.
     *
     * @param email recipient email
     * @param orderId order identifier
     * @param reason cancellation reason
     */
    void sendOrderCancelledEmail(String email, Long orderId, String reason);
}
