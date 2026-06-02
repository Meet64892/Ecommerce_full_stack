package com.smartshop.common.exception;

/**
 * ResourceNotFoundException - Signals missing domain resources.
 *
 * <h2>Purpose</h2>
 * This exception explicitly captures "not found" semantics, enabling REST handlers to return
 * HTTP 404 with structured details.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>HTTP mapping: domain exceptions map cleanly to status codes.</li>
 *   <li>Consumer clarity: precise errors reduce unnecessary retries.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Repository lookups throw this when Optional is empty after validation checks.
 *
 * @see BaseException
 * @author SmartShop Team
 */
public class ResourceNotFoundException extends BaseException {

    /**
     * Creates a not-found exception with SmartShop code.
     *
     * @param message details about which resource was missing
     */
    public ResourceNotFoundException(final String message) {
        super("RESOURCE_NOT_FOUND", message);
    }
}
