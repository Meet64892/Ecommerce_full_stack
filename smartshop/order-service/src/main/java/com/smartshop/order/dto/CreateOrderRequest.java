package com.smartshop.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * CreateOrderRequest - Payload for new order creation.
 *
 * <h2>Purpose</h2>
 * Bundles user identity and requested items for order submission API.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Nested validation: @Valid propagates validation to each order item.</li>
 *   <li>Boundary contract: immutable request model for service orchestration.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Received by OrderController and forwarded to OrderService for processing.
 *
 * @see com.smartshop.order.controller.OrderController
 * @author SmartShop Team
 */
public record CreateOrderRequest(
        @NotNull Long userId,
        @Valid @NotEmpty List<OrderItemDto> items
) {
}
