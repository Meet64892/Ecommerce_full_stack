package com.smartshop.common.exception;

import org.springframework.http.HttpStatus;

/**
 * ResourceNotFoundException - Thrown When a Requested Entity Does Not Exist
 *
 * <h2>Purpose</h2>
 * When a service looks up a User by ID or a Product by SKU and it doesn't exist,
 * this exception is thrown. GlobalExceptionHandler maps it to HTTP 404 Not Found.
 * Without this exception, services might throw NullPointerException (unhelpful)
 * or return HTTP 200 with null data (misleading to clients).
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>HTTP 404: Means "the resource you asked for doesn't exist on this server."
 *       Distinguished from 400 (bad request syntax) and 403 (forbidden).</li>
 *   <li>Meaningful error messages: Include the resource type and ID so developers
 *       can immediately understand what was missing without reading logs:
 *       "User with id=999 not found" is far better than "Not found".</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * UserServiceImpl throws this when findById returns empty Optional.
 * GlobalExceptionHandler catches BaseException (parent class) and returns 404.
 *
 * @author SmartShop Team
 */
public class ResourceNotFoundException extends BaseException {

    /**
     * Creates a ResourceNotFoundException with a formatted message.
     *
     * @param resourceType the type of resource (e.g., "User", "Product", "Order")
     * @param identifier   the ID or other identifier that was searched for
     */
    public ResourceNotFoundException(String resourceType, Object identifier) {
        super(
            String.format("%s with identifier '%s' was not found", resourceType, identifier),
            HttpStatus.NOT_FOUND,
            resourceType.toUpperCase().replace(" ", "_") + "_NOT_FOUND"
        );
    }

    /**
     * Creates a ResourceNotFoundException with a fully custom message.
     * Use when the standard "X with identifier Y was not found" pattern doesn't fit.
     *
     * @param message the complete error message to return
     */
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }
}
