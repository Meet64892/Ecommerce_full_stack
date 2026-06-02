package com.smartshop.inventory.service;

import com.smartshop.common.exception.ResourceNotFoundException;
import com.smartshop.inventory.dto.InventoryDto;
import com.smartshop.inventory.dto.ReserveStockRequest;
import com.smartshop.inventory.entity.Inventory;
import com.smartshop.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * InventoryServiceImpl - Inventory Management with Redis Caching and Optimistic Locking
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Cache-Aside Pattern (aka Lazy Loading):
 *       1. Read: Check Redis first → if cache hit, return cached data (fast, ~1ms)
 *          If cache miss, read from PostgreSQL → store in Redis → return data
 *       2. Write: Update PostgreSQL → evict Redis cache (force re-read on next access)
 *       Why cache-aside vs read-through?
 *       Read-through: cache handles DB reads (requires a caching framework that supports it)
 *       Cache-aside: application controls cache — more flexible, simpler to implement.</li>
 *   <li>@Cacheable(value = "inventory", key = "#productId"):
 *       Spring checks Redis for key "inventory::productId" before calling the method.
 *       If found, returns cached value (method body NOT executed).
 *       If not found, calls the method, caches the result, and returns it.</li>
 *   <li>@CacheEvict: Removes a specific cache entry.
 *       Must be called after writes to prevent stale data from being served.
 *       If not evicted, cached stock levels could show stale availability for up to TTL seconds.</li>
 *   <li>Optimistic Locking Retry: When ObjectOptimisticLockingFailureException is thrown
 *       (two transactions modified the same inventory row), we retry.
 *       @Retryable ensures the retry happens automatically without caller involvement.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl {

    private final InventoryRepository inventoryRepository;

    /**
     * Returns inventory data for a product, using Redis as cache.
     * Cache key = "inventory::{productId}". TTL = 5 minutes (configured in RedisConfig).
     * Cache hit avoids a PostgreSQL query — critical for product listing pages that
     * show stock availability for many products simultaneously.
     *
     * @param productId the product to check inventory for
     * @return the cached or freshly-loaded inventory DTO
     */
    @Cacheable(value = "inventory", key = "#productId")
    @Transactional(readOnly = true)
    public InventoryDto getInventoryByProductId(Long productId) {
        log.debug("Cache MISS for inventory productId={} — loading from database", productId);
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", productId));
        return toDto(inventory);
    }

    /**
     * Reserves stock for an order. Uses optimistic locking to handle concurrent reservations.
     * @Retryable: automatically retries if OptimisticLockingFailureException occurs.
     * maxAttempts=3: try up to 3 times before propagating the exception.
     * backoff: wait 100ms between retries with 2x multiplier (100ms, 200ms).
     *
     * WHY retry on OptimisticLockException?
     * T1 and T2 both read inventory at version=5. T1 commits (version → 6). T2 fails.
     * T2 retries: reads inventory at version=6, applies its change, commits (version → 7). Success.
     * This is safe because T2's re-read sees T1's change — no data loss.
     *
     * @param request the stock reservation request
     * @return updated inventory showing new reserved quantities
     */
    @Retryable(
        retryFor = ObjectOptimisticLockingFailureException.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional
    @CacheEvict(value = "inventory", key = "#request.productId")  // Evict stale cache after write
    public InventoryDto reserveStock(ReserveStockRequest request) {
        log.info("Reserving {} units of productId={} for orderId={}",
                request.quantity(), request.productId(), request.orderId());

        // Using standard findByProductId (with @Version field providing optimistic locking)
        // rather than findByProductIdForUpdate (pessimistic) to maximize throughput
        Inventory inventory = inventoryRepository.findByProductId(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", request.productId()));

        // reserve() validates available stock and increments reservedQuantity
        // Throws IllegalStateException if stock is insufficient
        inventory.reserve(request.quantity());

        Inventory saved = inventoryRepository.save(inventory);
        log.info("Reserved {} units of productId={}: availableNow={}",
                request.quantity(), request.productId(), saved.getAvailableQuantity());

        return toDto(saved);
    }

    /**
     * Releases reserved stock (compensating transaction for cancelled orders).
     * Called when order-service publishes OrderCancelledEvent.
     *
     * @param productId the product to release stock for
     * @param quantity  the amount to release back to available pool
     */
    @Transactional
    @CacheEvict(value = "inventory", key = "#productId")
    public void releaseReservedStock(Long productId, int quantity) {
        log.info("Releasing {} units of productId={}", quantity, productId);
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", productId));
        inventory.release(quantity);
        inventoryRepository.save(inventory);
    }

    /**
     * Updates stock quantity (for new deliveries, adjustments, etc.)
     * Also evicts cache to force fresh data on next read.
     *
     * @param productId the product to update
     * @param quantity  the NEW total quantity (not delta)
     * @return updated inventory DTO
     */
    @Transactional
    @CacheEvict(value = "inventory", key = "#productId")
    public InventoryDto updateStock(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", productId));
        inventory.setQuantity(quantity);
        return toDto(inventoryRepository.save(inventory));
    }

    /** Converts Inventory entity to InventoryDto */
    private InventoryDto toDto(Inventory inv) {
        return new InventoryDto(
                inv.getId(), inv.getProductId(), inv.getSku(),
                inv.getQuantity(), inv.getReservedQuantity(), inv.getAvailableQuantity(),
                inv.getLowStockThreshold(),
                inv.getAvailableQuantity() <= inv.getLowStockThreshold()
        );
    }
}
