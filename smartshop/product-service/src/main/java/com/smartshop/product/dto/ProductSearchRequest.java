package com.smartshop.product.dto;

/**
 * ProductSearchRequest - Search filter payload.
 *
 * <h2>Purpose</h2>
 * Encapsulates flexible search filters including keyword, price range, category, and rating.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Faceted search: combines multiple filter dimensions.</li>
 *   <li>Pagination strategy: supports scalable result navigation.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * ProductSearchService reads this request to compose filtered repository queries.
 *
 * @see com.smartshop.product.service.ProductSearchService
 * @author SmartShop Team
 */
public record ProductSearchRequest(
        String query,
        Double minPrice,
        Double maxPrice,
        Long categoryId,
        Integer rating,
        int page,
        int size
) {
}
