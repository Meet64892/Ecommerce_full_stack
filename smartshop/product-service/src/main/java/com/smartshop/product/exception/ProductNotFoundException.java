package com.smartshop.product.exception;

import com.smartshop.common.exception.ResourceNotFoundException;

/**
 * ProductNotFoundException - Not-found error for products and categories.
 *
 * <h2>Purpose</h2>
 * Catalog lookups are common and callers need clear 404 responses when a product or category id is invalid. A domain
 * exception keeps repository absence separate from unexpected infrastructure failures.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>HTTP 404: Communicates that the requested catalog resource does not exist.</li>
 *   <li>Optional handling: Services throw this after an empty Optional rather than calling get blindly.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * ProductServiceImpl throws it and GlobalExceptionHandler converts it to ErrorResponse.
 *
 * @see ResourceNotFoundException
 * @author SmartShop Team
 */
public class ProductNotFoundException extends ResourceNotFoundException {
    /**
     * Creates a not-found exception for a catalog resource.
     *
     * @param identifier product or category identifier
     */
    public ProductNotFoundException(Object identifier) {
        super("Product resource", identifier);
    }
}
