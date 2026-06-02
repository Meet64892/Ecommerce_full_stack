package com.smartshop.common.exception;

/**
 * ValidationException - Thrown for business-rule validation failures.
 *
 * <h2>Purpose</h2>
 * Bean Validation ({@code @NotNull} etc.) covers structural input checks, but
 * some rules are semantic (e.g. "order total must be positive", "cannot cancel a
 * delivered order"). This exception expresses those domain validation failures.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Maps to HTTP 400 (Bad Request) in handlers.</li>
 *   <li>Distinct from {@code ResourceNotFoundException} so callers/handlers can
 *       react differently.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Thrown by service layers when invariants are violated; converted to a 400
 * error body.
 *
 * @see BaseException
 * @author SmartShop Team
 */
public class ValidationException extends BaseException {

    /**
     * @param message explanation of which rule was violated
     */
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }
}
