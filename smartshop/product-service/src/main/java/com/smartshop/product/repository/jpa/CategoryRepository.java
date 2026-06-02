package com.smartshop.product.repository.jpa;

import com.smartshop.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * CategoryRepository - JPA repository for {@link Category} reference data.
 *
 * <h2>Purpose</h2>
 * CRUD over categories plus a name lookup used to prevent duplicates.
 *
 * <h2>How it fits in the system</h2>
 * Used by the category endpoints and during product creation to validate the
 * referenced category exists.
 *
 * @author SmartShop Team
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * @param name the category name
     * @return the category if present
     */
    Optional<Category> findByName(String name);
}
