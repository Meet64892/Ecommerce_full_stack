package com.smartshop.order.dto;

import com.smartshop.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * OrderDto - Safe Public Representation of an Order
 *
 * @author SmartShop Team
 */
public record OrderDto(
    Long id,
    Long userId,
    String correlationId,
    OrderStatus status,
    BigDecimal totalAmount,
    String shippingAddress,
    String notes,
    List<OrderItemDto> items,
    Instant createdAt,
    Instant updatedAt
) {}
