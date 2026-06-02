package com.smartshop.product.dto;

import jakarta.validation.constraints.*;

/**
 * ProductCreateRequest - Product create/update payload.
 *
 * <h2>Purpose</h2>
 * Bean Validation protects service logic from invalid catalog inputs before hitting persistence.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Declarative constraints: concise validation rules at the boundary.</li>
 *   <li>Record immutability: safe handoff through layered architecture.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * ProductController accepts this payload and delegates to ProductService.
 *
 * @see com.smartshop.product.controller.ProductController
 * @author SmartShop Team
 */
public record ProductCreateRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name max length is 255")
        String name,
        @NotBlank(message = "Description is required")
        @Size(max = 4000, message = "Description max length is 4000")
        String description,
        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        Double price,
        @NotNull(message = "Category is required")
        Long categoryId,
        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating min is 1")
        @Max(value = 5, message = "Rating max is 5")
        Integer rating
) {
}
