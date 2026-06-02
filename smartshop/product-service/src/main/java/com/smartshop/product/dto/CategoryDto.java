package com.smartshop.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * CategoryDto - Public category representation and simple create payload.
 *
 * <h2>Purpose</h2>
 * Category APIs need only a small immutable shape, so one record can represent both responses and create requests.
 * Validation ensures category names remain useful for navigation.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Facet: Categories are commonly used as search facets.</li>
 *   <li>Record DTO: Compact immutable carrier for API data.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * CategoryController returns this record and ProductDto references category id/name.
 *
 * @see com.smartshop.product.entity.Category
 * @author SmartShop Team
 */
public record CategoryDto(UUID id, @NotBlank @Size(max = 120) String name, @Size(max = 500) String description) {
}
