package com.smartshop.common.exception;

/**
 * ValidationException - Domain exception for invalid business input.
 *
 * <h2>Purpose</h2>
 * Bean Validation catches structural problems, while this exception captures business validation
 * that requires service-level knowledge such as duplicate SKUs or invalid state transitions.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Business validation: Rules that go beyond @NotNull and @Size annotations.</li>
 *   <li>Fail fast: Rejecting invalid commands before persistence protects data integrity.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Services throw ValidationException and exception handlers translate it to HTTP 400 responses.
 *
 * @see BaseException
 * @author SmartShop Team
 */
public class ValidationException extends BaseException {
    /**
     * Creates a validation exception with a stable code.
     *
     * @param message explanation of the violated business rule
     */
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }
}
