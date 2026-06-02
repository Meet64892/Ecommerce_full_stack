package com.smartshop.user.exception;

import com.smartshop.common.exception.ResourceNotFoundException;

/**
 * UserNotFoundException - Specialized not-found exception for user lookups.
 *
 * <h2>Purpose</h2>
 * A domain-specific type makes logs and exception handling clearer while reusing the common not-found behavior.
 * User identifiers are safe to include because they are already part of API paths.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Specialization: Domain names improve troubleshooting compared with generic exceptions.</li>
 *   <li>HTTP mapping: GlobalExceptionHandler converts this to 404.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * UserServiceImpl throws this when repository Optional results are empty.
 *
 * @see ResourceNotFoundException
 * @author SmartShop Team
 */
public class UserNotFoundException extends ResourceNotFoundException {
    /**
     * Creates a user not found exception.
     *
     * @param identifier id or email that failed lookup
     */
    public UserNotFoundException(Object identifier) {
        super("User", identifier);
    }
}
