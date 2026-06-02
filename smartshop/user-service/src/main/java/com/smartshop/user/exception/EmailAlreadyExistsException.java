package com.smartshop.user.exception;

import com.smartshop.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * EmailAlreadyExistsException - Thrown When Registration Email is Already Taken
 *
 * <h2>Purpose</h2>
 * Maps to HTTP 409 Conflict — the request conflicts with existing server state
 * (the email already exists). Distinct from 400 Bad Request (which means the
 * request format is wrong) and 422 Unprocessable Entity (business rule violation).
 *
 * @author SmartShop Team
 */
public class EmailAlreadyExistsException extends BaseException {

    public EmailAlreadyExistsException(String email) {
        super(
            "The email address '" + email + "' is already registered. Please use a different email or login.",
            HttpStatus.CONFLICT,
            "EMAIL_ALREADY_EXISTS"
        );
    }
}
