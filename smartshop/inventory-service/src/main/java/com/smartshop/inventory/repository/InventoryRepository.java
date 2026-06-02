package com.smartshop.inventory.repository;

import com.smartshop.inventory.entity.Inventory;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

/**
 * InventoryRepository - Persistence abstraction for stock records.
 *
 * <h2>Purpose</h2>
 * Provides inventory lookups with lock hints needed for safe concurrent reservations.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Optimistic lock mode: increments version and detects write conflicts.</li>
 *   <li>Pessimistic lock tradeoff: stronger serialization but lower throughput.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * InventoryService uses locked lookup when reserving stock.
 *
 * @see com.smartshop.inventory.entity.Inventory
 * @author SmartShop Team
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /**
     * Finds inventory by product id without lock for read-heavy paths.
     *
     * @param productId product identifier
     * @return optional inventory row
     */
    Optional<Inventory> findByProductId(Long productId);

    /**
     * Finds inventory with optimistic force increment lock for reservation writes.
     *
     * @param productId product id
     * @return optional inventory row
     */
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<Inventory> findWithLockingByProductId(Long productId);
}
