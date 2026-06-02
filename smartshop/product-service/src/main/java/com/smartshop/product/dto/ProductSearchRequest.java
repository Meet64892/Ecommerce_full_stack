package com.smartshop.product.dto;

import java.math.BigDecimal;

/**
 * ProductSearchRequest - Filter parameters for catalog search.
 *
 * <h2>Purpose</h2>
 * Bundles the optional search filters (free text, price range, category, minimum
 * rating) into one object. All fields are nullable so any subset can be applied.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Null = "no filter on this dimension", letting one endpoint serve many
 *       filter combinations.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Bound from query params by {@code ProductController} and passed to
 * {@code ProductSearchService}.
 *
 * @param query       free-text term to match in name/description
 * @param minPrice    inclusive lower price bound (nullable)
 * @param maxPrice    inclusive upper price bound (nullable)
 * @param categoryId  exact category filter (nullable)
 * @param minRating   minimum acceptable rating (nullable)
 * @author SmartShop Team
 */
public record ProductSearchRequest(
        String query,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Long categoryId,
        Double minRating
) {
}
