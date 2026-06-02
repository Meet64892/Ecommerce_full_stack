package com.smartshop.product.repository;

import com.smartshop.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ProductRepository - JPA repository for source-of-truth catalog writes.
 *
 * <h2>Purpose</h2>
 * Handles transactional product persistence in PostgreSQL.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Source of truth: relational DB retains canonical product state.</li>
 *   <li>Optional-based retrieval: explicit missing-data handling.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Used by ProductService for create/update/delete transactional operations.
 *
 * @see Product
 * @author SmartShop Team
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
}
