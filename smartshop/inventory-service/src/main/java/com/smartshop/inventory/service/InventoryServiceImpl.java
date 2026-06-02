package com.smartshop.inventory.service;

import com.smartshop.common.exception.ResourceNotFoundException;
import com.smartshop.inventory.dto.InventoryDto;
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
 * InventoryServiceImpl - Stock operations with caching + optimistic locking.
 *
 * <h2>Purpose</h2>
 * Serves cached stock reads and performs safe concurrent reservations.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>@Cacheable("inventory")</b>: implements the cache-aside read path —
 *       Spring checks Redis first; on a miss it runs the method, stores the
 *       result, and returns it.</li>
 *   <li><b>@CacheEvict</b>: on a successful reservation we invalidate the cached
 *       entry so the next read reflects the new quantity (event-driven eviction,
 *       complementing TTL as a safety net).</li>
 *   <li><b>Optimistic locking + retry</b>: a concurrent reservation may bump the
 *       {@code @Version}, causing {@code ObjectOptimisticLockingFailureException}.
 *       {@code @Retryable} transparently retries the whole reservation a few
 *       times — the losing thread re-reads the fresh quantity and tries again,
 *       eliminating the oversell race without holding DB locks.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Called by {@code InventoryController} and {@code OrderEventConsumer}.
 *
 * @author SmartShop Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    // Cache by productId; subsequent reads of the same product hit Redis.
    @Cacheable(value = "inventory", key = "#productId")
    public InventoryDto getStock(Long productId) {
        Inventory inv = inventoryRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("No inventory for product: " + productId));
        return new InventoryDto(inv.getProductId(), inv.getAvailableQuantity());
    }

    /**
     * {@inheritDoc}
     *
     * <p>Decrements stock atomically. If two requests race, the optimistic lock
     * makes one fail and {@code @Retryable} replays it against fresh data.
     */
    @Override
    @Transactional
    // Invalidate the cached entry so reads after a reservation are accurate.
    @CacheEvict(value = "inventory", key = "#productId")
    // Retry on optimistic-lock conflicts with a short exponential backoff.
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 4, backoff = @Backoff(delay = 50, multiplier = 2))
    public boolean reserve(Long productId, int quantity) {
        Inventory inv = inventoryRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("No inventory for product: " + productId));

        if (inv.getAvailableQuantity() < quantity) {
            log.warn("Insufficient stock for product {}: have {}, need {}",
                    productId, inv.getAvailableQuantity(), quantity);
            return false;
        }

        inv.setAvailableQuantity(inv.getAvailableQuantity() - quantity);
        // save() triggers a versioned UPDATE; a concurrent change throws and the
        // method is retried by @Retryable with the latest data.
        inventoryRepository.save(inv);
        log.info("Reserved {} units of product {} (remaining {})",
                quantity, productId, inv.getAvailableQuantity());
        return true;
    }
}
