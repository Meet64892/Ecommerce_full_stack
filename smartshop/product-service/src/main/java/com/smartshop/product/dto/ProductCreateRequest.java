package com.smartshop.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * ProductCreateRequest - Validated request for creating or updating products.
 *
 * <h2>Purpose</h2>
 * Request DTOs protect the domain model by validating external input at the boundary. The SKU pattern keeps product
 * identifiers integration-friendly and avoids whitespace/case surprises.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Bean Validation: Constraint annotations are enforced before the service layer executes.</li>
 *   <li>Price precision: BigDecimal is used for money-like values instead of floating point.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * ProductController accepts this record and ProductService persists a Product plus its search index document.
 *
 * @see ProductDto
 * @author SmartShop Team
 */
public record ProductCreateRequest(
        @NotBlank @Size(max = 200) String name,
        @NotBlank @Size(max = 2000) String description,
        @NotNull @DecimalMin(value = "0.01") BigDecimal price,
        @NotBlank @Pattern(regexp = "^[A-Z0-9-]+$") String stockKeepingUnit,
        @NotNull UUID categoryId,
        UUID brandId) {
}
