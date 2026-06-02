package com.smartshop.common.event.order;

import com.smartshop.common.event.BaseEvent;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * OrderCancelledEvent - Kafka Event Published When an Order is Cancelled
 *
 * <h2>Purpose</h2>
 * Compensating event: when inventory check fails or payment is rejected,
 * this event triggers compensating actions (release reserved inventory, etc.)
 * This is the "rollback" mechanism in the Saga pattern.
 *
 * @author SmartShop Team
 */
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelledEvent extends BaseEvent {

    private Long orderId;
    private Long userId;
    private String reason;

    public static OrderCancelledEvent of(Long orderId, Long userId, String reason, String correlationId) {
        OrderCancelledEvent event = OrderCancelledEvent.builder()
                .orderId(orderId)
                .userId(userId)
                .reason(reason)
                .build();
        event.initializeEvent("ORDER_CANCELLED", correlationId);
        return event;
    }
}
