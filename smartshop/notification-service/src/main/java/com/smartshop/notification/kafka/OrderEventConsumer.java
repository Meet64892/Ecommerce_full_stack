package com.smartshop.notification.kafka;

import com.smartshop.notification.service.EmailNotificationService;
import com.smartshop.common.event.order.OrderCancelledEvent;
import com.smartshop.common.event.order.OrderConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * OrderEventConsumer - Processes Order Kafka Events for Email Notifications
 *
 * <h2>Purpose</h2>
 * Listens for order events and triggers appropriate email notifications.
 * Each Kafka listener is in the "notification-service-group" consumer group,
 * meaning this service independently receives and processes ALL events —
 * even if inventory-service also consumes the same topics.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Consumer Group Independence: The "notification-service-group" is separate
 *       from "inventory-service-group". Kafka delivers every message to EVERY
 *       consumer group, so both services receive OrderConfirmedEvent independently.
 *       Inventory-service uses it to release/confirm stock; notification-service
 *       uses it to send an email. Same message, different purposes, no coordination needed.</li>
 *   <li>Dead Letter Topic: If email sending throws an exception and retries are exhausted,
 *       Spring Kafka moves the message to "order.confirmed.DLT" (by convention).
 *       An operations dashboard monitoring DLTs would alert the team to manually
 *       investigate and replay the notification.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final EmailNotificationService emailService;

    /**
     * Sends a confirmation email when an order is confirmed (inventory reserved).
     * The order confirmation email reassures the customer their order is being processed.
     *
     * @param event the OrderConfirmedEvent from order-service
     */
    @KafkaListener(
        topics = "order.confirmed",
        groupId = "notification-service-group"
    )
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        log.info("Sending confirmation email for orderId={} userId={}",
                event.getOrderId(), event.getUserId());

        // In a real implementation, fetch user email from user-service via REST call
        // or from the event if it carries the email. For now, we use a placeholder.
        String userEmail = event.getUserEmail() != null
                ? event.getUserEmail()
                : "customer@example.com";  // Placeholder

        Map<String, Object> variables = Map.of(
                "orderId", event.getOrderId(),
                "customerName", "Valued Customer",  // Would come from user-service
                "supportEmail", "support@smartshop.com"
        );

        emailService.sendTemplatedEmail(
                userEmail,
                "Order #" + event.getOrderId() + " Confirmed - SmartShop",
                "order-confirmed",  // Looks for templates/order-confirmed.html
                variables
        );
    }

    /**
     * Sends a cancellation email when an order is cancelled.
     * Includes the reason so customers understand what happened.
     *
     * @param event the OrderCancelledEvent from order-service
     */
    @KafkaListener(
        topics = "order.cancelled",
        groupId = "notification-service-group"
    )
    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("Sending cancellation email for orderId={}", event.getOrderId());

        Map<String, Object> variables = Map.of(
                "orderId", event.getOrderId(),
                "reason", event.getReason() != null ? event.getReason() : "Insufficient stock",
                "supportEmail", "support@smartshop.com"
        );

        // In production: fetch actual user email from user-service
        emailService.sendTemplatedEmail(
                "customer@example.com",
                "Order #" + event.getOrderId() + " Cancelled - SmartShop",
                "order-confirmed",  // Reuse template with different content
                variables
        );
    }
}
