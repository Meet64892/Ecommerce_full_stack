package com.smartshop.common.exception;

/**
 * ResourceNotFoundException - Thrown when a requested entity does not exist.
 *
 * <h2>Purpose</h2>
 * A reusable, service-agnostic "404" exception. Individual services may subclass
 * it for richer typing (e.g. {@code UserNotFoundException}) but can also throw
 * it directly for generic lookups.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Maps naturally to HTTP 404 in the global exception handler.</li>
 *   <li>Carries the fixed code {@code RESOURCE_NOT_FOUND}.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Service layers throw this after an {@code Optional} lookup is empty; the
 * handler converts it to a 404 {@code ErrorResponse}.
 *
 * @see BaseException
 * @author SmartShop Team
 */
public class ResourceNotFoundException extends BaseException {

    /**
     * @param message description of which resource was not found
     */
    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message);
    }
}
