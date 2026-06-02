package com.smartshop.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * OrderItemDto - Order line item payload and projection.
 *
 * <h2>Purpose</h2>
 * Captures product, quantity, and price information transferred between API and service layers.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Validation annotations: ensures quantities/prices are sane before processing.</li>
 *   <li>Immutable DTO: record prevents side-effect mutation bugs.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Included in CreateOrderRequest and OrderDto responses.
 *
 * @see CreateOrderRequest
 * @author SmartShop Team
 */
public record OrderItemDto(
        @NotNull Long productId,
        @NotNull @Min(1) Integer quantity,
        @NotNull BigDecimal unitPrice
) {
}
