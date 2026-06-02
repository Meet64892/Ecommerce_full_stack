package com.smartshop.product.service;

import com.smartshop.product.dto.ProductDto;
import com.smartshop.product.dto.ProductSearchRequest;
import com.smartshop.product.mapper.ProductMapper;
import com.smartshop.product.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * ProductSearchService - Dedicated Elasticsearch-backed search service.
 *
 * <h2>Purpose</h2>
 * Isolates search-specific query behavior from CRUD service, keeping code cohesive and easier to
 * optimize independently.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Pagination: Page includes total count, Slice skips expensive total calculation.</li>
 *   <li>Search delegation: keyword matching uses Elasticsearch repository methods.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * ProductController /products/search endpoint delegates to this service.
 *
 * @see ProductSearchRepository
 * @author SmartShop Team
 */
@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductSearchRepository productSearchRepository;
    private final ProductMapper productMapper;

    /**
     * Executes paginated keyword search.
     *
     * @param request search filters
     * @return paged product dto results
     */
    public Page<ProductDto> search(final ProductSearchRequest request) {
        final Pageable pageable = PageRequest.of(Math.max(request.page(), 0), Math.max(request.size(), 1));
        final String query = request.query() == null ? "" : request.query();
        return productSearchRepository.findByNameContainingIgnoreCase(query, pageable)
                .map(productMapper::toDto);
    }
}
