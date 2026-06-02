package com.smartshop.product.repository;

import com.smartshop.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * ProductSearchRepository - Elasticsearch repository for search workloads.
 *
 * <h2>Purpose</h2>
 * Elasticsearch uses inverted indexes for full-text search, making keyword and ranked queries
 * much faster than relational LIKE scans at scale.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Inverted index: token-to-document mapping for fast text retrieval.</li>
 *   <li>Shards/replicas: horizontal scalability and fault tolerance primitives.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * ProductSearchService queries this repository for search endpoints.
 *
 * @see com.smartshop.product.service.ProductSearchService
 * @author SmartShop Team
 */
public interface ProductSearchRepository extends ElasticsearchRepository<Product, Long> {

    /**
     * Performs case-insensitive keyword search by product name.
     *
     * @param name keyword fragment
     * @param pageable pagination configuration
     * @return paged search result
     */
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
