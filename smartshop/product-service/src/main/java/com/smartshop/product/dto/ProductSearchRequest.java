package com.smartshop.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * ProductSearchRequest - Search filters for catalog queries.
 *
 * <h2>Purpose</h2>
 * Search endpoints often combine text queries with filters such as category, price range, and rating. A record groups
 * those optional values so the service can apply filtering consistently.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Pagination: Pageable returns Page for total counts, while Slice can avoid count queries for infinite scroll.</li>
 *   <li>Faceted search: Filters narrow the full-text result set for shopper-friendly browsing.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * ProductController binds query parameters to this record and ProductSearchService queries Elasticsearch.
 *
 * @see com.smartshop.product.service.ProductSearchService
 * @author SmartShop Team
 */
public record ProductSearchRequest(String query,
                                   UUID categoryId,
                                   @DecimalMin("0.00") BigDecimal minPrice,
                                   @DecimalMin("0.00") BigDecimal maxPrice,
                                   @Min(0) @Max(5) Double minRating) {
}
