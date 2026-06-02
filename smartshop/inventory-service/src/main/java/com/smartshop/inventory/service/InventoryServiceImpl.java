package com.smartshop.inventory.service;

import com.smartshop.common.exception.ResourceNotFoundException;
import com.smartshop.common.exception.ValidationException;
import com.smartshop.inventory.dto.InventoryDto;
import com.smartshop.inventory.entity.Inventory;
import com.smartshop.inventory.repository.InventoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * InventoryServiceImpl - Implements stock reads and race-safe reservations.
 *
 * <h2>Purpose</h2>
 * The implementation combines Redis cache-aside reads with database-backed writes. Cache invalidation uses eviction on
 * successful writes because stale stock data can cause poor checkout decisions.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Race condition: Two requests can observe the same stock and both try to reserve it.</li>
 *   <li>@Version: Database-level optimistic locking works across JVMs, unlike synchronized blocks.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Controllers and Kafka consumers call this service, which updates PostgreSQL and lets Redis cache reads expire/evict.
 *
 * @see InventoryService
 * @author SmartShop Team
 */
@Service
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;

    /**
     * Creates the service with its repository dependency.
     *
     * @param inventoryRepository inventory persistence gateway
     */
    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Reads inventory using Redis cache-aside behavior.
     *
     * @param productId product id
     * @return inventory snapshot
     * @throws ResourceNotFoundException when no inventory row exists
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "inventory", key = "#productId")
    public InventoryDto getInventory(UUID productId) {
        return inventoryRepository.findByProductId(productId).map(this::toDto).orElseThrow(() -> new ResourceNotFoundException("Inventory", productId));
    }

    /**
     * Reserves stock and evicts the cached snapshot after a successful write.
     *
     * @param productId product id
     * @param quantity requested reservation quantity
     * @return updated inventory snapshot
     * @throws ValidationException when insufficient stock is available
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "inventory", key = "#productId")
    public InventoryDto reserve(UUID productId, int quantity) {
        Inventory inventory = inventoryRepository.findWithLockByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", productId));
        if (inventory.salableQuantity() < quantity) {
            throw new ValidationException("Insufficient stock for product " + productId);
        }
        // Optimistic locking is used here instead of synchronized because multiple service instances can reserve stock.
        inventory.setQuantityReserved(inventory.getQuantityReserved() + quantity);
        return toDto(inventory);
    }

    /**
     * Converts a persistent entity to an immutable API snapshot.
     *
     * @param inventory inventory entity
     * @return inventory DTO
     */
    private InventoryDto toDto(Inventory inventory) {
        return new InventoryDto(inventory.getProductId(), inventory.getQuantityAvailable(), inventory.getQuantityReserved(), inventory.salableQuantity(), inventory.getVersion());
    }
}
