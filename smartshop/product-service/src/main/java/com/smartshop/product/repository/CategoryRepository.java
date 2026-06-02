package com.smartshop.product.repository;

import com.smartshop.product.entity.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CategoryRepository - JPA repository for category reference data.
 *
 * <h2>Purpose</h2>
 * Provides CRUD access to product categories and uniqueness checks.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Reference data persistence: stable taxonomy for catalog filtering.</li>
 *   <li>Repository abstraction: no direct SQL in controller/service code.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * CategoryController and ProductService resolve category relations through this repository.
 *
 * @see Category
 * @author SmartShop Team
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds category by unique name.
     *
     * @param name category name
     * @return optional category
     */
    Optional<Category> findByName(String name);
}
