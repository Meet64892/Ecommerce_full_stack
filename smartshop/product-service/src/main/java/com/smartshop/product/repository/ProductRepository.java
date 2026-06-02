package com.smartshop.product.repository;

import com.smartshop.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * ProductRepository - Spring Data JPA Repository for Product Persistence
 *
 * <h2>Purpose</h2>
 * Provides SQL-based product queries against PostgreSQL. Used for:
 *   - CRUD operations (create, read, update, delete)
 *   - Structured queries (filter by price range, category, active status)
 *   - Pagination (Page<T> for large result sets)
 *
 * Note: Full-text search queries go to ProductSearchRepository (Elasticsearch).
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Page{@code <T>} vs Slice{@code <T>}:
 *       Page{@code <T>}: includes total count (SELECT COUNT(*)) → needed for "Page 3 of 50" UI
 *       Slice{@code <T>}: no total count → lighter query, good for infinite scroll
 *       Use Slice when you don't need the total count (mobile feed, infinite scroll).
 *       Use Page when you show page numbers (web UI with pagination controls).</li>
 *   <li>Pageable parameter: Spring automatically maps query params to Pageable:
 *       GET /products?page=2&size=10&sort=price,desc
 *       → Pageable { pageNumber=2, pageSize=10, sort=price DESC }</li>
 * </ul>
 *
 * @author SmartShop Team
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Returns all active products in a category with pagination.
     * Used for category browsing (not full-text search).
     *
     * @param categoryId the category to filter by
     * @param pageable   pagination and sorting parameters
     * @return a Page of active products in the category
     */
    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    /**
     * Returns all active products within a price range with pagination.
     * Used for price filter UI.
     *
     * @param minPrice minimum price (inclusive)
     * @param maxPrice maximum price (inclusive)
     * @param pageable pagination parameters
     * @return a Page of products in the price range
     */
    Page<Product> findByPriceBetweenAndActiveTrue(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Finds a product by its SKU (stock keeping unit).
     *
     * @param sku the unique SKU string
     * @return Optional containing the product if found
     */
    Optional<Product> findBySku(String sku);

    /**
     * Returns all active products with pagination for the catalog listing.
     *
     * @param pageable pagination parameters
     * @return a Page of active products
     */
    Page<Product> findByActiveTrue(Pageable pageable);

    /**
     * Complex filtered query using JPQL.
     * All parameters are optional — if null, the condition is skipped.
     * This pattern allows flexible filtering without N different query methods.
     *
     * @param categoryId optional category filter (null = any category)
     * @param minPrice   optional minimum price (null = no minimum)
     * @param maxPrice   optional maximum price (null = no maximum)
     * @param minRating  optional minimum rating (null = any rating)
     * @param pageable   pagination parameters
     * @return filtered, paginated products
     */
    @Query("SELECT p FROM Product p WHERE p.active = true " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:minRating IS NULL OR p.rating >= :minRating)")
    Page<Product> findWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minRating") Double minRating,
            Pageable pageable);
}
