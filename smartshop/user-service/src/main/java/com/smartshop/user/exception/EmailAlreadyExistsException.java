package com.smartshop.user.exception;

import com.smartshop.common.exception.ValidationException;

/**
 * EmailAlreadyExistsException - Business error for duplicate registration emails.
 *
 * <h2>Purpose</h2>
 * Email is the login username, so it must be unique. A dedicated exception lets clients show a clear message instead
 * of a generic database constraint error.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Uniqueness rule: Enforced both in service logic and by the database unique index.</li>
 *   <li>Validation exception: Mapped to HTTP 400 because the submitted command is invalid.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * UserServiceImpl throws this before saving a new account with an existing email.
 *
 * @see ValidationException
 * @author SmartShop Team
 */
public class EmailAlreadyExistsException extends ValidationException {
    /**
     * Creates the duplicate email exception.
     *
     * @param email email address that already exists
     */
    public EmailAlreadyExistsException(String email) {
        super("Email already exists: " + email);
    }
}
