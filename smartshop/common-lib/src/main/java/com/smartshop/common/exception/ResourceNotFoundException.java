package com.smartshop.common.exception;

/**
 * ResourceNotFoundException - Domain exception for missing resources.
 *
 * <h2>Purpose</h2>
 * Missing rows or documents are expected business outcomes, not programming bugs. This type lets
 * services consistently map that situation to HTTP 404.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>HTTP semantics: Resource absence maps naturally to 404 Not Found.</li>
 *   <li>Domain boundary: Services decide which missing resource names are safe to expose.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Repositories return Optional values and services throw this exception when the Optional is empty.
 *
 * @see BaseException
 * @author SmartShop Team
 */
public class ResourceNotFoundException extends BaseException {
    /**
     * Creates a not-found error with a consistent code.
     *
     * @param resourceName the business name of the missing resource
     * @param identifier the identifier that failed lookup
     */
    public ResourceNotFoundException(String resourceName, Object identifier) {
        super("RESOURCE_NOT_FOUND", resourceName + " not found for identifier: " + identifier);
    }
}
