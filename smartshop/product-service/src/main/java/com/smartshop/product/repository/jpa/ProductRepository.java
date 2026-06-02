package com.smartshop.product.repository.jpa;

import com.smartshop.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ProductRepository - JPA repository (PostgreSQL source of truth) for products.
 *
 * <h2>Purpose</h2>
 * Persists the authoritative product rows and supports paginated listing.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Pageable / Page&lt;T&gt;</b>: {@code Pageable} carries page number,
 *       size, and sort. {@code Page<T>} returns the slice PLUS total count and
 *       total pages (an extra COUNT query). {@code Slice<T>} is the lighter
 *       cousin: it only knows whether a next page exists (no total count), which
 *       is cheaper for infinite-scroll UIs.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Lives in the {@code .repository.jpa} package so it is picked up by JPA
 * repository scanning (not Elasticsearch scanning).
 *
 * @author SmartShop Team
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Lists products in a category with paging/sorting.
     *
     * @param categoryId the category to filter by
     * @param pageable   page number, size, and sort
     * @return a {@link Page} of matching products including total counts
     */
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
}
