package com.smartshop.product.exception;

import com.smartshop.common.exception.ResourceNotFoundException;

/**
 * ProductNotFoundException - Thrown when a product id does not exist.
 *
 * <h2>Purpose</h2>
 * Typed 404 for the product context, reusing the shared resource-not-found code.
 *
 * <h2>How it fits in the system</h2>
 * Thrown by {@code ProductServiceImpl}; mapped to 404 by the handler.
 *
 * @author SmartShop Team
 */
public class ProductNotFoundException extends ResourceNotFoundException {

    /**
     * @param id the missing product id
     */
    public ProductNotFoundException(Long id) {
        super("Product not found: " + id);
    }
}
