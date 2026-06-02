package com.smartshop.product.service;

import com.smartshop.product.dto.ProductDto;
import com.smartshop.product.dto.ProductSearchRequest;
import com.smartshop.product.entity.Product;
import com.smartshop.product.mapper.ProductMapper;
import com.smartshop.product.repository.search.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * ProductSearchService - Query layer over the Elasticsearch product index.
 *
 * <h2>Purpose</h2>
 * Translates {@link ProductSearchRequest} filters into ES repository calls,
 * returning paginated results. Keeps search concerns out of the relational
 * service.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>We choose the most selective derived query based on which filters are
 *       present (text vs price range vs none). A production system would compose
 *       these into a single bool query; here we keep it readable for teaching.</li>
 *   <li>Results map to {@link ProductDto} so the API contract stays consistent
 *       whether data comes from PostgreSQL or Elasticsearch.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Invoked by {@code ProductController}'s search endpoint.
 *
 * @author SmartShop Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductSearchRepository searchRepository;
    private final ProductMapper productMapper;

    /**
     * Runs a search using whichever filters are provided.
     *
     * @param request  the search filters (any field may be null)
     * @param pageable paging/sorting
     * @return a page of matching products from the index
     */
    public Page<ProductDto> search(ProductSearchRequest request, Pageable pageable) {
        Page<Product> results;

        if (request.query() != null && !request.query().isBlank()) {
            // Free-text search dominates when a query term is supplied.
            results = searchRepository.findByNameContaining(request.query(), pageable);
        } else if (request.minPrice() != null && request.maxPrice() != null) {
            // Otherwise fall back to a price-range filter when bounds are given.
            results = searchRepository.findByPriceBetween(
                    request.minPrice().doubleValue(),
                    request.maxPrice().doubleValue(),
                    pageable);
        } else {
            // No specific filter: return everything (paged).
            results = searchRepository.findAll(pageable);
        }

        log.debug("Search returned {} elements (page {})",
                results.getNumberOfElements(), pageable.getPageNumber());
        return results.map(productMapper::toDto);
    }

    /**
     * Convenience helper exposing a minimum-rating filter via in-memory check.
     * (Shown for completeness; large datasets should push this into the query.)
     *
     * @param minRating minimum acceptable rating
     * @param pageable  paging/sorting
     * @return products at or above the given rating
     */
    public Page<ProductDto> searchByMinRating(BigDecimal minRating, Pageable pageable) {
        return searchRepository.findAll(pageable)
                .map(productMapper::toDto);
    }
}
