package com.smartshop.product.repository;

import com.smartshop.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * ProductSearchRepository - Spring Data Elasticsearch Repository
 *
 * <h2>Purpose</h2>
 * Provides Elasticsearch-based search operations for the Product @Document.
 * Works identically to JpaRepository but against Elasticsearch instead of PostgreSQL.
 * Spring Data generates the implementation automatically from method names.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>ElasticsearchRepository: Spring Data's equivalent of JpaRepository but for ES.
 *       Provides save/findById/findAll + generates query methods from method names.
 *       Under the hood, it converts method names to Elasticsearch Query DSL JSON.</li>
 *   <li>Shards and Replicas: Elasticsearch distributes data across shards.
 *       A shard is a Lucene instance — one node can hold multiple shards.
 *       Replicas are copies of shards for high availability and read throughput.
 *       For a development single-node setup, replicas must be 0 (no other node to replicate to).</li>
 *   <li>Eventual Consistency: After saving to PostgreSQL and then Elasticsearch,
 *       there's a brief window where the DB has the product but ES doesn't yet.
 *       For product search, this is acceptable — the product will appear in search
 *       results within milliseconds, not seconds.</li>
 * </ul>
 *
 * @see Product @Document annotation
 * @author SmartShop Team
 */
public interface ProductSearchRepository extends ElasticsearchRepository<Product, Long> {

    /**
     * Searches products by name using Elasticsearch's full-text analysis.
     * Spring Data generates: {"query": {"match": {"name": "query"}}}
     * The "standard" analyzer applied to the name field will tokenize and match.
     *
     * @param name     the search term
     * @param pageable pagination parameters
     * @return a Page of matching products
     */
    Page<Product> findByName(String name, Pageable pageable);

    /**
     * Finds products by category ID in Elasticsearch.
     * Used when we want Elasticsearch pagination on category results.
     *
     * @param categoryId the category to filter by
     * @param pageable   pagination parameters
     * @return a Page of products in the category
     */
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * Finds active products by brand.
     * Useful for brand-specific search pages.
     *
     * @param brand    the brand name (exact match — brand is KEYWORD type)
     * @param active   only return active products
     * @param pageable pagination parameters
     * @return matching products
     */
    Page<Product> findByBrandAndActiveIsTrue(String brand, Pageable pageable);
}
