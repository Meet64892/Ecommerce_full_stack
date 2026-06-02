package com.smartshop.common.exception;

/**
 * ValidationException - Signals domain-level validation errors.
 *
 * <h2>Purpose</h2>
 * Bean Validation catches structural errors, while this exception captures cross-field or
 * business invariants that require service-layer context.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Domain validation: protects business rules beyond DTO constraints.</li>
 *   <li>Consistent error taxonomy: handlers return explicit VALIDATION_ERROR code.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Service methods throw this before persistence when invariants are violated.
 *
 * @see BaseException
 * @author SmartShop Team
 */
public class ValidationException extends BaseException {

    /**
     * Constructs a validation exception.
     *
     * @param message reason the request violates business rules
     */
    public ValidationException(final String message) {
        super("VALIDATION_ERROR", message);
    }
}
