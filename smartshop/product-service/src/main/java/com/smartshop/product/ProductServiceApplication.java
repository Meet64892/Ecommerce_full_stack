package com.smartshop.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ProductServiceApplication - Starts catalog and search functionality.
 *
 * <h2>Purpose</h2>
 * Product-service owns catalog data and exposes APIs for browsing, searching, and managing products. It stores
 * normalized product data in PostgreSQL while indexing searchable fields in Elasticsearch for fast full-text queries.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Dual persistence: PostgreSQL is the source of truth, Elasticsearch is the search projection.</li>
 *   <li>Bounded context: Catalog concerns are isolated from orders and inventory.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Clients call product APIs through the gateway; order-service later references product ids in order items.
 *
 * @see com.smartshop.product.service.ProductService
 * @author SmartShop Team
 */
@SpringBootApplication
public class ProductServiceApplication {
    /**
     * Starts the product service.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
