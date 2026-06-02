package com.smartshop.order.dto;

import com.smartshop.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * OrderDto - Outbound representation of an order.
 *
 * <h2>Purpose</h2>
 * A safe, flattened view of the order aggregate for API responses, decoupled
 * from the JPA entity.
 *
 * @param id          order id
 * @param userId      owning customer
 * @param status      current lifecycle status
 * @param totalAmount order total
 * @param items       line items
 * @param createdAt   creation timestamp
 * @author SmartShop Team
 */
public record OrderDto(
        Long id,
        Long userId,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemDto> items,
        Instant createdAt
) {
}
