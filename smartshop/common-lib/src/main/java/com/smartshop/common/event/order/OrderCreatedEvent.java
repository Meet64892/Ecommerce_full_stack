package com.smartshop.common.event.order;

import com.smartshop.common.event.BaseEvent;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

/**
 * OrderCreatedEvent - Kafka Event Published When a New Order is Placed
 *
 * <h2>Purpose</h2>
 * Published to the "order.created" Kafka topic when a new order enters PENDING state.
 * inventory-service consumes this event to reserve stock.
 * notification-service consumes this to send order confirmation to the customer.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Kafka Topics: A topic is a named category/feed of messages.
 *       Producers write to topics; consumers read from topics.
 *       Topics are partitioned for parallel processing and replicated for fault tolerance.</li>
 *   <li>At-Least-Once Delivery: Kafka guarantees that every message is delivered
 *       at least once (with retries). This means consumers may receive the same message
 *       twice in failure scenarios. Consumers MUST be idempotent — processing the same
 *       event twice should produce the same result (not double-reserve inventory).</li>
 *   <li>Idempotency via eventId: Consumers track processed eventIds in Redis or DB.
 *       If an eventId was already processed, skip it. This handles at-least-once delivery.</li>
 *   <li>Event as an Immutable Fact: Once published, this event cannot be changed.
 *       If an error occurred, publish a NEW compensating event (OrderCancelledEvent).
 *       Never edit or delete published events.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent extends BaseEvent {

    /** The order ID — consumers can call order-service to get full details if needed */
    private Long orderId;

    /** The customer who placed the order */
    private Long userId;

    /** Total order amount for payment processing */
    private BigDecimal totalAmount;

    /** Line items — inventory-service needs these to reserve stock */
    private List<OrderItemEvent> items;

    /** Shipping address for the delivery */
    private String shippingAddress;

    /**
     * Factory method to create an OrderCreatedEvent from domain objects.
     * Using factory methods on the event class keeps the event creation logic centralized.
     *
     * @param orderId       the newly created order ID
     * @param userId        the customer placing the order
     * @param totalAmount   the order total
     * @param items         the ordered items
     * @param shippingAddress the delivery address
     * @param correlationId the saga correlation ID
     * @return a fully initialized OrderCreatedEvent
     */
    public static OrderCreatedEvent of(Long orderId, Long userId, BigDecimal totalAmount,
                                        List<OrderItemEvent> items, String shippingAddress,
                                        String correlationId) {
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(orderId)
                .userId(userId)
                .totalAmount(totalAmount)
                .items(items)
                .shippingAddress(shippingAddress)
                .build();
        event.initializeEvent("ORDER_CREATED", correlationId);
        return event;
    }

    /**
     * Nested DTO for order item data within the event.
     * Contains only what consumers need — avoids oversharing internal data.
     */
    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemEvent {
        private Long productId;
        private String sku;
        private Integer quantity;
        private BigDecimal unitPrice;
    }
}
