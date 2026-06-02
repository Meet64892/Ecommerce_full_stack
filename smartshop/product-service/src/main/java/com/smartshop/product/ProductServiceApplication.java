package com.smartshop.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * ProductServiceApplication - Bootstraps the product catalog service.
 *
 * <h2>Purpose</h2>
 * Manages products and categories with a dual-store strategy: PostgreSQL is the
 * authoritative relational store; Elasticsearch powers full-text/faceted search.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Separate repository scanning</b>: because we mix JPA and Elasticsearch
 *       repositories, we point each {@code @Enable...Repositories} at its own
 *       package so Spring assigns each interface to the correct store and does
 *       not try to back an ES repository with JPA (or vice versa).</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Registers as {@code product-service}; the gateway routes {@code /api/products/**}
 * (programmatic route) here.
 *
 * @author SmartShop Team
 */
@SpringBootApplication
// Scan JPA repositories only under the .repository.jpa package.
@EnableJpaRepositories(basePackages = "com.smartshop.product.repository.jpa")
// Scan Elasticsearch repositories only under the .repository.search package.
@EnableElasticsearchRepositories(basePackages = "com.smartshop.product.repository.search")
public class ProductServiceApplication {

    /**
     * Spring Boot entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
