package com.smartshop.product.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * ProductDto - Safe Public Representation of a Product
 *
 * @author SmartShop Team
 */
public record ProductDto(
    Long id,
    String name,
    String description,
    String sku,
    BigDecimal price,
    BigDecimal originalPrice,
    String brand,
    Double rating,
    Integer ratingCount,
    Long categoryId,
    String categoryName,
    String imageUrl,
    boolean active,
    Instant createdAt,
    Instant updatedAt
) {}
