package com.smartshop.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * CreateOrderRequest - Validated payload to place an order.
 *
 * <h2>Purpose</h2>
 * Captures who is ordering and what line items they want.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@code @Valid} on the list cascades validation into each
 *       {@link OrderItemDto} (nested validation).</li>
 *   <li>{@code @NotEmpty} guarantees at least one line item.</li>
 * </ul>
 *
 * @param userId the customer placing the order
 * @param items  the requested line items (at least one)
 * @author SmartShop Team
 */
public record CreateOrderRequest(

        @NotNull(message = "userId is required")
        Long userId,

        @NotEmpty(message = "an order must contain at least one item")
        @Valid
        List<OrderItemDto> items
) {
}
