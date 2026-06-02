package com.smartshop.user.exception;

import com.smartshop.common.exception.ResourceNotFoundException;

/**
 * UserNotFoundException - Thrown when a requested user does not exist.
 *
 * <h2>Purpose</h2>
 * A typed, domain-specific 404 for the users context. Extending the shared
 * {@code ResourceNotFoundException} keeps the platform-wide error code
 * ({@code RESOURCE_NOT_FOUND}) while giving call sites a precise type to throw.
 *
 * <h2>How it fits in the system</h2>
 * Thrown by {@code UserServiceImpl} after an empty repository lookup; mapped to
 * 404 by the GlobalExceptionHandler.
 *
 * @see ResourceNotFoundException
 * @author SmartShop Team
 */
public class UserNotFoundException extends ResourceNotFoundException {

    /**
     * @param identifier the id or email that was not found
     */
    public UserNotFoundException(String identifier) {
        super("User not found: " + identifier);
    }
}
