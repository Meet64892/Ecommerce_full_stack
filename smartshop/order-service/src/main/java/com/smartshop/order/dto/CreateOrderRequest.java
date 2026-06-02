package com.smartshop.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * CreateOrderRequest - Checkout command sent by clients.
 *
 * <h2>Purpose</h2>
 * The request groups the buyer id and desired items into one command. Bean Validation prevents starting an expensive
 * distributed Saga for obviously invalid input.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Nested validation: @Valid validates every OrderItemDto inside the list.</li>
 *   <li>Command object: Represents user intent rather than database state.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * OrderController receives this record and OrderServiceImpl creates the pending order aggregate.
 *
 * @see OrderDto
 * @author SmartShop Team
 */
public record CreateOrderRequest(@NotNull UUID userId, @NotEmpty List<@Valid OrderItemDto> items) {
}
