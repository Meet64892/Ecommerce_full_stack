package com.smartshop.common.event.order;

import com.smartshop.common.event.BaseEvent;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * OrderConfirmedEvent - Kafka Event Published When an Order is Confirmed
 *
 * <h2>Purpose</h2>
 * Published after inventory is successfully reserved and payment authorized.
 * notification-service consumes this to send a "Your order is confirmed!" email.
 *
 * @author SmartShop Team
 */
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderConfirmedEvent extends BaseEvent {

    private Long orderId;
    private Long userId;
    private String userEmail;

    public static OrderConfirmedEvent of(Long orderId, Long userId, String userEmail, String correlationId) {
        OrderConfirmedEvent event = OrderConfirmedEvent.builder()
                .orderId(orderId)
                .userId(userId)
                .userEmail(userEmail)
                .build();
        event.initializeEvent("ORDER_CONFIRMED", correlationId);
        return event;
    }
}
