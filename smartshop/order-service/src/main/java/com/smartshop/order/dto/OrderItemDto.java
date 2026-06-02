package com.smartshop.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * OrderItemDto - DTO for Order Line Items
 *
 * @author SmartShop Team
 */
public record OrderItemDto(
    Long id,
    Long productId,
    String productName,
    String sku,
    Integer quantity,
    BigDecimal unitPrice
) {}
