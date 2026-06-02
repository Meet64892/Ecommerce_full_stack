package com.smartshop.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * OrderItemDto - A line item in create-order requests and order responses.
 *
 * <h2>Purpose</h2>
 * Carries the product, quantity, and unit price for one line. Reused for both
 * input (validated) and output for simplicity.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@code @Min(1)} ensures a positive quantity — you can't order zero/neg.</li>
 * </ul>
 *
 * @param productId catalog product id
 * @param quantity  number of units (>= 1)
 * @param unitPrice price per unit at purchase time
 * @author SmartShop Team
 */
public record OrderItemDto(

        @NotNull(message = "productId is required")
        Long productId,

        @Min(value = 1, message = "quantity must be at least 1")
        int quantity,

        @NotNull(message = "unitPrice is required")
        BigDecimal unitPrice
) {
}
