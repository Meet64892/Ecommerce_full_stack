package com.smartshop.notification.kafka;

import com.smartshop.common.util.JsonUtils;
import com.smartshop.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * OrderEventConsumer - Consumes order lifecycle events for notifications.
 *
 * <h2>Purpose</h2>
 * Notifications should not be direct HTTP calls from order-service because email providers are slow and occasionally
 * unavailable. Kafka provides fire-and-forget decoupling and lets notifications retry independently.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Consumer group: This service receives every order event independently of analytics or support consumers.</li>
 *   <li>Dead letter topic: Repeatedly failing notification messages should be moved aside for investigation.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * The consumer parses Kafka JSON payloads and delegates delivery to NotificationService.
 *
 * @see NotificationService
 * @author SmartShop Team
 */
@Slf4j
@Component
public class OrderEventConsumer {
    private final NotificationService notificationService;

    /**
     * Creates the consumer with the notification delivery abstraction.
     *
     * @param notificationService notification delivery service
     */
    public OrderEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Handles confirmed order events.
     *
     * @param payload JSON payload from Kafka
     */
    @KafkaListener(topics = "order.confirmed", groupId = "notification-service")
    public void onOrderConfirmed(String payload) {
        OrderConfirmedEvent event = JsonUtils.fromJson(payload, OrderConfirmedEvent.class);
        log.info("Sending order confirmation for {}", event.getOrderId());
        notificationService.sendOrderConfirmed(event.getOrderId(), event.getUserId());
    }

    /**
     * Handles cancelled order events.
     *
     * @param payload JSON payload from Kafka
     */
    @KafkaListener(topics = "order.cancelled", groupId = "notification-service")
    public void onOrderCancelled(String payload) {
        OrderCancelledEvent event = JsonUtils.fromJson(payload, OrderCancelledEvent.class);
        log.info("Sending order cancellation for {}", event.getOrderId());
        notificationService.sendOrderCancelled(event.getOrderId(), event.getUserId(), event.getReason());
    }
}
