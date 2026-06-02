package com.smartshop.product.repository;

import com.smartshop.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

/**
 * ProductSearchRepository - Elasticsearch repository for product search documents.
 *
 * <h2>Purpose</h2>
 * Elasticsearch stores an inverted index: terms point to documents, making full-text search fast compared with SQL
 * `LIKE` scans. Shards split an index for scale, and replicas provide redundancy and read throughput.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Inverted index: Maps words to matching product documents.</li>
 *   <li>Repository abstraction: Spring Data creates search queries from method names.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * ProductSearchService calls this repository to search indexed products, while ProductServiceImpl keeps it updated.
 *
 * @see Product
 * @author SmartShop Team
 */
public interface ProductSearchRepository extends ElasticsearchRepository<Product, UUID> {
    /**
     * Searches product name or description for the supplied text.
     *
     * @param name text matched against the analyzed name field
     * @param description text matched against the analyzed description field
     * @param pageable pagination request
     * @return page of matching products
     */
    Page<Product> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);
}
