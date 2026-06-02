package com.smartshop.user.exception;

/**
 * EmailAlreadyExistsException - Thrown on duplicate email registration.
 *
 * <h2>Purpose</h2>
 * Protects unique email constraint at domain layer before persistence exception bubbles up.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Proactive validation: better UX than raw database constraint error.</li>
 *   <li>Business rule enforcement: one account per email policy.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Raised by UserService registration flow and handled centrally by advice.
 *
 * @see GlobalExceptionHandler
 * @author SmartShop Team
 */
public class EmailAlreadyExistsException extends RuntimeException {

    /**
     * Creates duplicate-email exception.
     *
     * @param message domain context
     */
    public EmailAlreadyExistsException(final String message) {
        super(message);
    }
}
