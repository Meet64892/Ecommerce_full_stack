package com.smartshop.user.exception;

/**
 * UserNotFoundException - Thrown when user does not exist.
 *
 * <h2>Purpose</h2>
 * Dedicated exception enables clear mapping to HTTP 404 and explicit client behavior.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Semantic exceptions: easier error handling and observability tagging.</li>
 *   <li>Fail-fast retrieval: Optional empty is converted immediately.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Thrown from UserService when repository lookups fail.
 *
 * @see com.smartshop.user.exception.GlobalExceptionHandler
 * @author SmartShop Team
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Creates user-not-found exception.
     *
     * @param message domain context
     */
    public UserNotFoundException(final String message) {
        super(message);
    }
}
