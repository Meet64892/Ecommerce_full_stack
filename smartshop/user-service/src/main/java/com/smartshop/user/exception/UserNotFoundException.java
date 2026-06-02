package com.smartshop.user.exception;

import com.smartshop.common.exception.ResourceNotFoundException;

/**
 * UserNotFoundException - Thrown When a User Cannot Be Found
 *
 * <h2>Purpose</h2>
 * Specific exception for user lookup failures. Extends ResourceNotFoundException
 * which maps to HTTP 404 Not Found.
 *
 * @author SmartShop Team
 */
public class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException(Long id) {
        super("User", id);
    }

    public UserNotFoundException(String emailOrUsername) {
        super("User with email/username '" + emailOrUsername + "' was not found");
    }
}
