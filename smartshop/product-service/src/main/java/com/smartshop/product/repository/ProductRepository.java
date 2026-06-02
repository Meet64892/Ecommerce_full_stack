package com.smartshop.product.repository;

import com.smartshop.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * ProductRepository - JPA repository for transactional catalog data.
 *
 * <h2>Purpose</h2>
 * PostgreSQL is the source of truth for product data because relational constraints and transactions protect catalog
 * integrity. The repository exposes CRUD operations while Optional prevents null-related lookup bugs.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Source of truth: Search indexes can be rebuilt from this database if needed.</li>
 *   <li>Derived queries: existsByStockKeepingUnit becomes a SQL uniqueness check.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * ProductServiceImpl uses this repository before writing the Elasticsearch projection.
 *
 * @see Product
 * @author SmartShop Team
 */
public interface ProductRepository extends JpaRepository<Product, UUID> {
    /**
     * Finds a product by SKU for integration lookups.
     *
     * @param stockKeepingUnit unique SKU
     * @return Optional containing the product when present
     */
    Optional<Product> findByStockKeepingUnit(String stockKeepingUnit);

    /**
     * Checks for duplicate SKUs before create.
     *
     * @param stockKeepingUnit candidate SKU
     * @return true when the SKU is already used
     */
    boolean existsByStockKeepingUnit(String stockKeepingUnit);
}
