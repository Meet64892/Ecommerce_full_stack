package com.smartshop.inventory.repository;

import com.smartshop.inventory.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

/**
 * InventoryRepository - JPA access layer for inventory rows.
 *
 * <h2>Purpose</h2>
 * Stock updates must load the current row and rely on the @Version field to detect concurrent reservations. Optional
 * return types force service code to handle missing inventory explicitly.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>@Lock: Documents the intended concurrency strategy for reservation reads.</li>
 *   <li>Optimistic conflict: Hibernate throws when another transaction updates the row first.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * InventoryServiceImpl uses this repository inside transactions for reads and reservations.
 *
 * @see Inventory
 * @author SmartShop Team
 */
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    /**
     * Finds inventory by product id for public API reads.
     *
     * @param productId product id
     * @return Optional containing inventory when configured
     */
    Optional<Inventory> findByProductId(UUID productId);

    /**
     * Finds inventory by product id for reservation writes.
     *
     * @param productId product id
     * @return Optional containing inventory when configured
     */
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Inventory> findWithLockByProductId(UUID productId);
}
