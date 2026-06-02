package com.smartshop.product.dto;

import java.math.BigDecimal;

/**
 * ProductDto - Outbound product representation.
 *
 * <h2>Purpose</h2>
 * Decouples the API contract from the dual-annotated {@code Product} entity so
 * persistence concerns (JPA/ES annotations) never leak to clients.
 *
 * <h2>How it fits in the system</h2>
 * Produced by {@code ProductMapper} and returned by the controllers.
 *
 * @param id          product id
 * @param name        display name
 * @param description long description
 * @param price       unit price
 * @param categoryId  owning category id
 * @param rating      average rating
 * @author SmartShop Team
 */
public record ProductDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Long categoryId,
        Double rating
) {
}
