package com.smartshop.notification.kafka;

import com.smartshop.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * OrderEventConsumer - Consumes order confirmation/cancellation events.
 *
 * <h2>Purpose</h2>
 * Handles notification side effects for order state transitions.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Independent consumer group: receives every event stream separately.</li>
 *   <li>DLT strategy: poison messages should be routed to dead-letter topics in production.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Receives order events and invokes NotificationService methods.
 *
 * @see com.smartshop.notification.service.NotificationService
 * @author SmartShop Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final NotificationService notificationService;

    /**
     * Processes order confirmed events.
     *
     * @param event confirmed payload
     */
    @KafkaListener(topics = "order.confirmed", groupId = "notification-service-group")
    public void onOrderConfirmed(final OrderConfirmedEvent event) {
        notificationService.sendOrderConfirmedEmail("customer@example.com", event.orderId());
        log.info("Processed order.confirmed for order {}", event.orderId());
    }

    /**
     * Processes order cancelled events.
     *
     * @param event cancellation payload
     */
    @KafkaListener(topics = "order.cancelled", groupId = "notification-service-group")
    public void onOrderCancelled(final OrderCancelledEvent event) {
        notificationService.sendOrderCancelledEmail("customer@example.com", event.orderId(), event.reason());
        log.info("Processed order.cancelled for order {}", event.orderId());
    }
}
