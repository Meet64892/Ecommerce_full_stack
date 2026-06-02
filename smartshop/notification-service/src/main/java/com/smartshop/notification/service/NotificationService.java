package com.smartshop.notification.service;

/**
 * NotificationService - Abstraction over notification channels.
 *
 * <h2>Purpose</h2>
 * Defines a channel-agnostic contract for sending a notification, so consumers
 * depend on an interface, not a concrete email implementation. Adding SMS/push
 * later means a new implementation, not changes to callers.
 *
 * <h2>How it fits in the system</h2>
 * Implemented by {@code EmailNotificationService}; called by the Kafka consumers.
 *
 * @author SmartShop Team
 */
public interface NotificationService {

    /**
     * Sends a notification with a pre-rendered HTML body.
     *
     * @param to       recipient address
     * @param subject  message subject
     * @param htmlBody rendered HTML content
     */
    void send(String to, String subject, String htmlBody);
}
