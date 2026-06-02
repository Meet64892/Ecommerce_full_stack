package com.smartshop.notification.kafka;

import com.smartshop.notification.event.OrderCancelledEvent;
import com.smartshop.notification.event.OrderConfirmedEvent;
import com.smartshop.notification.service.NotificationService;
import com.smartshop.notification.template.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * OrderEventConsumer - Sends emails reacting to order lifecycle events.
 *
 * <h2>Purpose</h2>
 * Listens to {@code order.confirmed} and {@code order.cancelled} and emails the
 * customer the appropriate message rendered from a Thymeleaf template.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Two listeners, one per topic, in the same consumer group.</li>
 *   <li>Side effects (emails) are decoupled from the order service via events.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Consumes order events; renders + sends via {@code EmailNotificationService}.
 *
 * @author SmartShop Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final NotificationService notificationService;
    private final EmailTemplateService templateService;

    /**
     * Sends a confirmation email when an order is confirmed.
     *
     * @param event the order-confirmed event
     */
    @KafkaListener(topics = "order.confirmed", groupId = "notification-service")
    public void onOrderConfirmed(OrderConfirmedEvent event) {
        log.info("Consumed ORDER_CONFIRMED for order {} (eventId={})", event.getOrderId(), event.getEventId());
        String body = templateService.render("order-confirmed",
                Map.of("orderId", event.getOrderId(), "userId", event.getUserId()));
        // In a full system we'd look up the user's email; here we derive a stub.
        notificationService.send("user-" + event.getUserId() + "@smartshop.dev",
                "Your SmartShop order #" + event.getOrderId() + " is confirmed", body);
    }

    /**
     * Sends a cancellation email when an order is cancelled.
     *
     * @param event the order-cancelled event
     */
    @KafkaListener(topics = "order.cancelled", groupId = "notification-service")
    public void onOrderCancelled(OrderCancelledEvent event) {
        log.info("Consumed ORDER_CANCELLED for order {} reason={}", event.getOrderId(), event.getReason());
        String body = templateService.render("order-confirmed",
                Map.of("orderId", event.getOrderId(), "userId", event.getUserId(),
                        "cancelled", true, "reason", String.valueOf(event.getReason())));
        notificationService.send("user-" + event.getUserId() + "@smartshop.dev",
                "Your SmartShop order #" + event.getOrderId() + " was cancelled", body);
    }
}
