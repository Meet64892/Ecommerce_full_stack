package com.smartshop.product.exception;

/**
 * ProductNotFoundException - Signals missing product/category resources.
 *
 * <h2>Purpose</h2>
 * Converts absent catalog resources into clear domain exceptions instead of null errors.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Domain semantics: enriches missing-resource context for API clients.</li>
 *   <li>Centralized translation: handled by REST advice as HTTP 404.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Thrown by ProductService when requested entities are absent.
 *
 * @see GlobalExceptionHandler
 * @author SmartShop Team
 */
public class ProductNotFoundException extends RuntimeException {

    /**
     * Creates not-found exception with context.
     *
     * @param message details about missing resource
     */
    public ProductNotFoundException(final String message) {
        super(message);
    }
}
