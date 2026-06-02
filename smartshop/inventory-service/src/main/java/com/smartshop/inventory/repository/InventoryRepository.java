package com.smartshop.inventory.repository;

import com.smartshop.inventory.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * InventoryRepository - JPA repository for stock rows.
 *
 * <h2>Purpose</h2>
 * Standard CRUD plus a pessimistically-locked lookup for the rare hot-row case.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>@Lock(PESSIMISTIC_WRITE)</b>: issues {@code SELECT ... FOR UPDATE},
 *       locking the row so concurrent transactions queue behind us. The entity's
 *       {@code @Version} already gives optimistic locking for the common path;
 *       this method is provided to demonstrate the pessimistic alternative for
 *       extreme-contention products (e.g. a flash sale on one SKU).</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Used by {@code InventoryServiceImpl}; the default {@code findById} path relies
 * on optimistic locking via {@code @Version}.
 *
 * @author SmartShop Team
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /**
     * Pessimistically locks and returns the stock row for a product.
     *
     * @param productId the product id
     * @return the locked inventory row, if present
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.productId = :productId")
    Optional<Inventory> findByIdForUpdate(@Param("productId") Long productId);
}
