package com.smartshop.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * CreateOrderRequest - Validated DTO for Creating a New Order
 *
 * @author SmartShop Team
 */
public record CreateOrderRequest(

    @NotBlank(message = "Shipping address is required")
    @Size(max = 500, message = "Shipping address too long")
    String shippingAddress,

    @Size(max = 1000, message = "Notes too long")
    String notes,

    @NotEmpty(message = "Order must contain at least one item")
    @Valid  // Cascade validation to each item in the list
    List<OrderItemRequest> items
) {

    /**
     * Nested record for order item requests.
     * Immutable value objects — records are perfect for this.
     */
    public record OrderItemRequest(
        @NotNull(message = "Product ID is required")
        @Positive(message = "Product ID must be positive")
        Long productId,

        @NotBlank(message = "SKU is required")
        String sku,

        @NotBlank(message = "Product name is required")
        String productName,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 100, message = "Maximum 100 units per item per order")
        Integer quantity,

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.01", message = "Price must be positive")
        BigDecimal unitPrice
    ) {}
}
