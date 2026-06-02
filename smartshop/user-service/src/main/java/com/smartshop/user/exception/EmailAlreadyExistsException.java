package com.smartshop.user.exception;

import com.smartshop.common.exception.BaseException;

/**
 * EmailAlreadyExistsException - Thrown when registering a duplicate email.
 *
 * <h2>Purpose</h2>
 * Emails are the unique login identifier. Attempting to register an existing one
 * is a client conflict (HTTP 409), distinct from a generic validation error.
 *
 * <h2>How it fits in the system</h2>
 * Thrown by {@code UserServiceImpl.register}; mapped to 409 by the handler.
 *
 * @see BaseException
 * @author SmartShop Team
 */
public class EmailAlreadyExistsException extends BaseException {

    /**
     * @param email the email that is already taken
     */
    public EmailAlreadyExistsException(String email) {
        super("EMAIL_ALREADY_EXISTS", "Email already registered: " + email);
    }
}
