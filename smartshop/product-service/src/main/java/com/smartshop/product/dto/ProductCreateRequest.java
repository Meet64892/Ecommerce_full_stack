package com.smartshop.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * ProductCreateRequest - Validated payload to create a product.
 *
 * <h2>Purpose</h2>
 * Captures the fields needed to add a catalog item, with Bean Validation guarding
 * required values and a positive price.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@code @DecimalMin("0.0", inclusive=false)} rejects zero/negative prices.</li>
 * </ul>
 *
 * @param name        required product name
 * @param description optional description (bounded length)
 * @param price       required positive price
 * @param categoryId  required category reference
 * @author SmartShop Team
 */
public record ProductCreateRequest(

        @NotBlank(message = "name must not be blank")
        @Size(max = 200, message = "name must be at most 200 characters")
        String name,

        @Size(max = 2000, message = "description must be at most 2000 characters")
        String description,

        @NotNull(message = "price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "price must be greater than 0")
        BigDecimal price,

        @NotNull(message = "categoryId is required")
        Long categoryId
) {
}
