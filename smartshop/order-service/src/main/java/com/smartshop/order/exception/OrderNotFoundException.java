package com.smartshop.order.exception;

/**
 * OrderNotFoundException - Thrown when order does not exist.
 *
 * <h2>Purpose</h2>
 * Domain-specific exception for cleaner 404 API semantics.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Explicit domain errors improve client handling and telemetry.</li>
 *   <li>Avoids leaking persistence exceptions to API consumers.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Raised by OrderService read paths when lookup fails.
 *
 * @see GlobalExceptionHandler
 * @author SmartShop Team
 */
public class OrderNotFoundException extends RuntimeException {

    /**
     * Constructs not-found exception.
     *
     * @param message error context
     */
    public OrderNotFoundException(final String message) {
        super(message);
    }
}
