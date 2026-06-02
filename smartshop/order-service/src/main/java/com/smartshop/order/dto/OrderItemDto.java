package com.smartshop.order.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * OrderItemDto - API representation of an order line item.
 *
 * <h2>Purpose</h2>
 * The DTO captures only the data needed to create and display order items. Validation ensures quantities and prices
 * are positive before an order enters the Saga flow.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Immutable command data: Records prevent accidental mutation during request handling.</li>
 *   <li>Validation: @Min and @DecimalMin reject invalid checkout data early.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * CreateOrderRequest contains a list of these records and OrderCreatedEvent reuses their product and quantity data.
 *
 * @see CreateOrderRequest
 * @author SmartShop Team
 */
public record OrderItemDto(UUID id, @NotNull UUID productId, @Min(1) int quantity,
                           @NotNull @DecimalMin("0.01") BigDecimal unitPrice) {
}
