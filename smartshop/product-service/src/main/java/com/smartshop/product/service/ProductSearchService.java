package com.smartshop.product.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.smartshop.product.dto.ProductSearchRequest;
import com.smartshop.product.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ProductSearchService - Elasticsearch Full-Text Search Implementation
 *
 * <h2>Purpose</h2>
 * Provides full-text search capabilities over the product catalog using
 * Elasticsearch's query DSL. This service handles complex search scenarios
 * that PostgreSQL LIKE queries can't efficiently solve.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Bool Query: Elasticsearch's compound query type.
 *       Combines multiple sub-queries with boolean logic:
 *       - must: required conditions (AND) — affect relevance score
 *       - filter: required conditions (AND) — do NOT affect relevance score (faster)
 *       - should: optional conditions (OR) — boost score if matched
 *       - must_not: excluded conditions (NOT)</li>
 *   <li>Multi-Match Query: Searches multiple fields simultaneously.
 *       "red shoes" matches products where name OR description contains these words.
 *       Field boosting: name^2 means matches in name count twice as much as description.</li>
 *   <li>Range Query: For numeric filters (price range, minimum rating).
 *       More efficient than script queries because ES uses BKD trees for range lookups.</li>
 *   <li>Filter vs Query Context: Queries in "filter" context are:
 *       - Faster (Elasticsearch caches filter results)
 *       - Don't affect relevance scoring
 *       Use filter for: price range, category, active status
 *       Use query for: full-text search (affects relevance)</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSearchService {

    // ElasticsearchClient: the modern Java Elasticsearch client (replaces RestHighLevelClient)
    private final ElasticsearchClient elasticsearchClient;

    /**
     * Performs a full-text search with optional filters against the Elasticsearch products index.
     * Builds a Boolean query combining text search, price range, category, and rating filters.
     *
     * @param searchRequest the search parameters (query, filters)
     * @param pageable      pagination and sorting
     * @return a paginated Page of matching products
     * @throws RuntimeException wrapping IOException on Elasticsearch communication failure
     */
    public Page<Product> search(ProductSearchRequest searchRequest, Pageable pageable) {
        log.debug("Executing ES search: query={}, category={}, priceRange={}-{}",
                searchRequest.query(), searchRequest.categoryId(),
                searchRequest.minPrice(), searchRequest.maxPrice());

        try {
            // Build the Bool query with text search and filters
            BoolQuery.Builder boolQuery = new BoolQuery.Builder();

            // Full-text search: if query is provided, search name and description
            if (searchRequest.query() != null && !searchRequest.query().isBlank()) {
                // multi_match: search the same query text across multiple fields
                // name^2: matches in name are twice as relevant as matches in description
                boolQuery.must(Query.of(q -> q
                        .multiMatch(mm -> mm
                                .query(searchRequest.query())
                                .fields("name^2", "description", "brand")
                        )
                ));
            } else {
                // No text query → match all products (filters will narrow results)
                boolQuery.must(Query.of(q -> q.matchAll(ma -> ma)));
            }

            // Filter: only active products (filters don't affect relevance score)
            boolQuery.filter(Query.of(q -> q.term(t -> t.field("active").value(true))));

            // Filter: category
            if (searchRequest.categoryId() != null) {
                boolQuery.filter(Query.of(q -> q
                        .term(t -> t.field("categoryId").value(searchRequest.categoryId()))
                ));
            }

            // Filter: brand exact match
            if (searchRequest.brand() != null && !searchRequest.brand().isBlank()) {
                boolQuery.filter(Query.of(q -> q
                        .term(t -> t.field("brand.keyword").value(searchRequest.brand()))
                ));
            }

            // Filter: price range
            if (searchRequest.minPrice() != null || searchRequest.maxPrice() != null) {
                boolQuery.filter(Query.of(q -> q
                        .range(r -> {
                            r.field("price");
                            if (searchRequest.minPrice() != null) {
                                r.gte(co.elastic.clients.json.JsonData.of(searchRequest.minPrice()));
                            }
                            if (searchRequest.maxPrice() != null) {
                                r.lte(co.elastic.clients.json.JsonData.of(searchRequest.maxPrice()));
                            }
                            return r;
                        })
                ));
            }

            // Filter: minimum rating
            if (searchRequest.minRating() != null) {
                boolQuery.filter(Query.of(q -> q
                        .range(r -> r.field("rating")
                                .gte(co.elastic.clients.json.JsonData.of(searchRequest.minRating())))
                ));
            }

            // Build and execute the search request
            SearchRequest esRequest = SearchRequest.of(s -> s
                    .index("products")
                    .query(Query.of(q -> q.bool(boolQuery.build())))
                    .from((int) pageable.getOffset())   // Pagination offset
                    .size(pageable.getPageSize())        // Page size
            );

            SearchResponse<Product> response = elasticsearchClient.search(esRequest, Product.class);

            // Extract the product objects from the search hits
            List<Product> products = response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

            // Total hits for pagination (safe null check)
            long totalHits = response.hits().total() != null
                    ? response.hits().total().value()
                    : 0;

            return new PageImpl<>(products, pageable, totalHits);

        } catch (IOException e) {
            log.error("Elasticsearch search failed: {}", e.getMessage(), e);
            throw new RuntimeException("Search service temporarily unavailable", e);
        }
    }

    /**
     * Indexes or updates a product document in Elasticsearch.
     * Called by ProductServiceImpl after every create/update to keep ES in sync.
     *
     * @param product the product to index
     */
    public void indexProduct(Product product) {
        try {
            // Denormalize category name for search (ES can't do JOINs)
            if (product.getCategory() != null) {
                product.setCategoryName(product.getCategory().getName());
            }
            elasticsearchClient.index(i -> i
                    .index("products")
                    .id(String.valueOf(product.getId()))
                    .document(product)
            );
            log.debug("Indexed product id={} in Elasticsearch", product.getId());
        } catch (IOException e) {
            // Log but don't throw — a failed ES index is not a transaction-level failure.
            // The product IS saved in PostgreSQL. The ES index will be eventually consistent
            // (could be re-synced via a background job or Kafka consumer).
            log.error("Failed to index product id={} in Elasticsearch: {}", product.getId(), e.getMessage());
        }
    }

    /**
     * Removes a product document from the Elasticsearch index.
     * Called when a product is deleted or deactivated.
     *
     * @param productId the ID of the product to remove from the index
     */
    public void removeProductFromIndex(Long productId) {
        try {
            elasticsearchClient.delete(d -> d
                    .index("products")
                    .id(String.valueOf(productId))
            );
            log.debug("Removed product id={} from Elasticsearch index", productId);
        } catch (IOException e) {
            log.error("Failed to remove product id={} from Elasticsearch: {}", productId, e.getMessage());
        }
    }
}
