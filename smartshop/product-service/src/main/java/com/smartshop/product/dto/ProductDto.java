package com.smartshop.product.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * ProductDto - Public catalog product representation.
 *
 * <h2>Purpose</h2>
 * The DTO flattens category information so API clients do not need to understand JPA relationships. It also prevents
 * accidental lazy-loading serialization problems that can happen when exposing entities directly.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>DTO flattening: categoryId and categoryName are exposed instead of a nested entity graph.</li>
 *   <li>Immutable record: Product responses are data snapshots, not mutable domain objects.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * ProductMapper creates this record for controller responses.
 *
 * @see com.smartshop.product.mapper.ProductMapper
 * @author SmartShop Team
 */
public record ProductDto(UUID id, String name, String description, BigDecimal price, String stockKeepingUnit,
                         double rating, UUID categoryId, String categoryName) {
}
