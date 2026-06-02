package com.smartshop.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * ReserveStockRequest - Request to Reserve Inventory for an Order
 *
 * @author SmartShop Team
 */
public record ReserveStockRequest(

    @NotNull(message = "Product ID is required")
    @Positive
    Long productId,

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Must reserve at least 1 unit")
    Integer quantity,

    /** Order ID for idempotency — same orderId + productId should not double-reserve */
    @NotNull
    Long orderId
) {}
