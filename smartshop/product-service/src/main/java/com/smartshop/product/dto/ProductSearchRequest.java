package com.smartshop.product.dto;

import java.math.BigDecimal;

/**
 * ProductSearchRequest - Full-Text Search Query Parameters
 *
 * <h2>Purpose</h2>
 * Encapsulates all search filters that can be applied when searching products.
 * Passed to ProductSearchService which builds the Elasticsearch query.
 *
 * @author SmartShop Team
 */
public record ProductSearchRequest(

    /**
     * Full-text search query — matched against name, description, brand.
     * Null = no text search (return all products matching other filters).
     */
    String query,

    /**
     * Filter by category ID.
     * Null = all categories.
     */
    Long categoryId,

    /**
     * Minimum price filter.
     * Null = no minimum.
     */
    BigDecimal minPrice,

    /**
     * Maximum price filter.
     * Null = no maximum.
     */
    BigDecimal maxPrice,

    /**
     * Minimum average rating (0.0 - 5.0).
     * Null = any rating.
     */
    Double minRating,

    /**
     * Brand name for exact filtering.
     * Null = all brands.
     */
    String brand
) {}
