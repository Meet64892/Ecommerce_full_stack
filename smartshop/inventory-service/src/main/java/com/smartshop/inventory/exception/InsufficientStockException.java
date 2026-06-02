package com.smartshop.inventory.exception;

/**
 * InsufficientStockException - Signals stock cannot satisfy reservation request.
 *
 * <h2>Purpose</h2>
 * Distinguishes business stock failures from system failures for clean saga cancellation logic.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Business exception: expected domain outcome under demand spikes.</li>
 *   <li>Graceful failure: order saga can compensate using this explicit signal.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Thrown by reserve flow and translated into inventory.checked=false events.
 *
 * @see com.smartshop.inventory.kafka.OrderEventConsumer
 * @author SmartShop Team
 */
public class InsufficientStockException extends RuntimeException {

    /**
     * Creates insufficient stock exception.
     *
     * @param message failure reason
     */
    public InsufficientStockException(final String message) {
        super(message);
    }
}
