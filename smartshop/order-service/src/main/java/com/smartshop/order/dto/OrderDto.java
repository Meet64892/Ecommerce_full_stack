package com.smartshop.order.dto;

import com.smartshop.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * OrderDto - Public representation of an order.
 *
 * <h2>Purpose</h2>
 * The DTO gives clients order state without exposing JPA internals or lazy-loading proxies. It includes items and the
 * current Saga status so users can see whether checkout is pending, confirmed, or cancelled.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Eventual consistency: Status may be PENDING until inventory replies asynchronously.</li>
 *   <li>Aggregate projection: One DTO represents the order and child line items.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * OrderServiceImpl maps Order aggregates to this record for controller responses.
 *
 * @see com.smartshop.order.entity.Order
 * @author SmartShop Team
 */
public record OrderDto(UUID id, UUID userId, OrderStatus status, BigDecimal totalAmount, List<OrderItemDto> items, Instant createdAt) {
}
