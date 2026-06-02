package com.smartshop.product.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * ProductCreateRequest - Validated DTO for Product Creation
 *
 * @author SmartShop Team
 */
public record ProductCreateRequest(

    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must not exceed 200 characters")
    String name,

    @Size(max = 5000, message = "Description too long")
    String description,

    @NotBlank(message = "SKU is required")
    @Pattern(regexp = "^[A-Z0-9-]{3,50}$", message = "SKU must be uppercase alphanumeric with hyphens")
    String sku,

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "9999999.99", message = "Price exceeds maximum allowed value")
    BigDecimal price,

    BigDecimal originalPrice,

    @Size(max = 100)
    String brand,

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be positive")
    Long categoryId,

    String imageUrl
) {}
