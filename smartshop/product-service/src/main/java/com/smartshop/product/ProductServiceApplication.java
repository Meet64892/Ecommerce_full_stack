package com.smartshop.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * ProductServiceApplication - Product Catalog Service
 *
 * <h2>Purpose</h2>
 * Manages the product catalog: CRUD operations via PostgreSQL (structured data)
 * and full-text search via Elasticsearch (inverted index for fast keyword search).
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Dual Persistence Strategy (PostgreSQL + Elasticsearch):
 *       PostgreSQL handles: structured queries (by category, price range), transactions, relationships
 *       Elasticsearch handles: full-text search ("red running shoes"), faceted search, relevance ranking
 *       Write to BOTH on create/update — eventual consistency between them is acceptable
 *       because search results being slightly stale is tolerable, but product data must be accurate.</li>
 *   <li>Why not just use Elasticsearch for everything?
 *       Elasticsearch is NOT a primary database: no ACID transactions, complex joins are expensive,
 *       schema changes require reindexing, and it's optimized for reads not writes at high throughput.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
