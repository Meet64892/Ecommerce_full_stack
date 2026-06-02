package com.smartshop.product.repository.search;

import com.smartshop.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * ProductSearchRepository - Elasticsearch repository for product search.
 *
 * <h2>Purpose</h2>
 * Backs full-text and range queries against the {@code products} index. The
 * derived query methods below are translated by Spring Data Elasticsearch into
 * native ES queries.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>ElasticsearchRepository</b>: like a JPA repository but persists to ES;
 *       {@code save}/{@code findAll} operate on the index.</li>
 *   <li><b>Derived ES queries</b>: {@code findByNameContaining} becomes a
 *       match/wildcard query on the analyzed {@code name} text field; price-range
 *       methods become ES range queries.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Lives in {@code .repository.search} so Elasticsearch repository scanning picks
 * it up (not JPA scanning). Used by {@code ProductSearchService}.
 *
 * @author SmartShop Team
 */
@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<Product, Long> {

    /**
     * Full-text-ish search by name substring.
     *
     * @param name     the term to search within product names
     * @param pageable paging/sorting
     * @return a page of matching products from the index
     */
    Page<Product> findByNameContaining(String name, Pageable pageable);

    /**
     * Filters products to an inclusive price band.
     *
     * @param min      minimum price (inclusive)
     * @param max      maximum price (inclusive)
     * @param pageable paging/sorting
     * @return a page of products within the band
     */
    Page<Product> findByPriceBetween(double min, double max, Pageable pageable);
}
