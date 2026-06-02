package com.smartshop.product.repository;

import com.smartshop.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * CategoryRepository - JPA repository for product categories.
 *
 * <h2>Purpose</h2>
 * Categories are persisted independently so products can reference a stable category id. Repository methods keep the
 * service layer free from boilerplate SQL.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Unique category names: Avoid duplicate navigation facets.</li>
 *   <li>Optional: Forces explicit handling when a category id is invalid.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * ProductServiceImpl loads categories when creating products, and CategoryController manages category records.
 *
 * @see Category
 * @author SmartShop Team
 */
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    /**
     * Finds a category by display name.
     *
     * @param name category name
     * @return Optional containing a category when present
     */
    Optional<Category> findByName(String name);
}
