package com.smartshop.inventory.service;

import com.smartshop.inventory.dto.InventoryDto;
import com.smartshop.inventory.entity.Inventory;
import com.smartshop.inventory.exception.InsufficientStockException;
import com.smartshop.inventory.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * InventoryServiceImpl - Inventory service implementation with cache and locking.
 *
 * <h2>Purpose</h2>
 * Uses cache-aside strategy: reads check cache first, misses load from DB and cache result.
 * Writes update DB then evict cache entry to avoid stale responses.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Cache invalidation: TTL plus explicit eviction after writes.</li>
 *   <li>Optimistic locking vs pessimistic locking: optimistic chosen for high read concurrency.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Serves REST reads and Kafka-driven reservation updates.
 *
 * @see com.smartshop.inventory.config.RedisConfig
 * @author SmartShop Team
 */
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    /**
     * Returns inventory, caching frequent reads in Redis.
     *
     * @param productId product id
     * @return inventory dto
     */
    @Override
    @Cacheable(cacheNames = "inventory", key = "#productId")
    public InventoryDto getInventory(final Long productId) {
        final Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found for product: " + productId));
        return toDto(inventory);
    }

    /**
     * Reserves stock atomically with optimistic locking.
     *
     * @param productId product id
     * @param quantity units to reserve
     * @return updated inventory dto
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "inventory", key = "#productId")
    public InventoryDto reserveStock(final Long productId, final Integer quantity) {
        final Inventory inventory = inventoryRepository.findWithLockingByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found for product: " + productId));

        // We use optimistic locking because synchronized only protects one JVM, while @Version
        // detects conflicts across multiple service instances sharing the same database.
        if (inventory.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException("Not enough stock for product: " + productId);
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        return toDto(inventoryRepository.save(inventory));
    }

    /**
     * Maps entity to DTO.
     *
     * @param inventory entity
     * @return dto
     */
    private InventoryDto toDto(final Inventory inventory) {
        return new InventoryDto(inventory.getProductId(), inventory.getAvailableQuantity(), inventory.getReservedQuantity());
    }
}
