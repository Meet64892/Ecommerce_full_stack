package com.smartshop.order.dto;

import com.smartshop.order.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * OrderDto - External order projection.
 *
 * <h2>Purpose</h2>
 * Provides clients with current order status and immutable line-item summary.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Status visibility: communicates asynchronous saga progress to clients.</li>
 *   <li>Stable API shape: shields clients from internal entity changes.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Returned by order API endpoints and polling UIs.
 *
 * @see com.smartshop.order.entity.Order
 * @author SmartShop Team
 */
public record OrderDto(Long id, Long userId, OrderStatus status, BigDecimal totalAmount, List<OrderItemDto> items, Instant createdAt) {
}
