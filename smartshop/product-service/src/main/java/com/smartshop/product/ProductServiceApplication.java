package com.smartshop.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ProductServiceApplication - Product catalog and search service entry point.
 *
 * <h2>Purpose</h2>
 * This bounded context manages products, categories, and search capabilities. It combines
 * relational consistency for transactions with Elasticsearch for relevance-ranked search.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Dual persistence: PostgreSQL as source of truth plus Elasticsearch read model.</li>
 *   <li>Catalog domain: products and categories managed in one cohesive service.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Gateway routes product APIs here; order-service references product data for order items.
 *
 * @see com.smartshop.product.service.ProductServiceImpl
 * @author SmartShop Team
 */
@SpringBootApplication
public class ProductServiceApplication {

    /**
     * Starts product service application.
     *
     * @param args runtime args
     * @return nothing; Spring owns lifecycle
     */
    public static void main(final String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
