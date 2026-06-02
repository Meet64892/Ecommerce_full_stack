package com.smartshop.inventory.repository;

import com.smartshop.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

/**
 * InventoryRepository - JPA Repository for Inventory Persistence
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>@Lock(LockModeType.OPTIMISTIC): Explicitly requests optimistic locking
 *       for queries (the @Version field already provides optimistic locking,
 *       but this annotation adds version checking to read queries too).</li>
 *   <li>LockModeType.PESSIMISTIC_WRITE: Adds SELECT ... FOR UPDATE in SQL.
 *       Blocks other transactions from reading/writing this row until the
 *       current transaction commits. Use for high-contention resources where
 *       optimistic locking would cause too many retries.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);

    Optional<Inventory> findBySku(String sku);

    /**
     * Finds inventory by product ID with PESSIMISTIC WRITE lock.
     * Use this method when you need to reserve stock and want to prevent
     * concurrent reads until your transaction commits.
     * Use sparingly — it reduces throughput on the inventory table.
     *
     * @param productId the product to lock inventory for
     * @return the locked inventory record
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.productId = :productId")
    Optional<Inventory> findByProductIdForUpdate(@Param("productId") Long productId);
}
