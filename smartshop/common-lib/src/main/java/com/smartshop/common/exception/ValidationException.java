package com.smartshop.common.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * ValidationException - Thrown When Business-Level Validation Fails
 *
 * <h2>Purpose</h2>
 * Bean Validation (@NotNull, @Size, etc.) catches structural validation at the
 * HTTP layer. But some validation rules require database state or business logic —
 * e.g., "email must be unique", "order total cannot be zero", "product must be
 * in an active category". These business validations happen in the service layer
 * and throw ValidationException, which maps to HTTP 422 Unprocessable Entity.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>422 vs 400: HTTP 400 means the request syntax is wrong (missing required field).
 *       HTTP 422 means the syntax is valid but the business rules are violated.
 *       Using the correct status code helps clients distinguish "fix your request
 *       format" from "fix your business data".</li>
 *   <li>Multiple violations: A single request might violate multiple business rules.
 *       The details list allows reporting all violations at once rather than forcing
 *       the client to fix one, resubmit, discover another, fix that, etc.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
public class ValidationException extends BaseException {

    /** All business rule violations found in this request */
    private final List<String> violations;

    /**
     * Creates a ValidationException with a single violation message.
     *
     * @param message the business rule violation description
     */
    public ValidationException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_FAILED");
        this.violations = List.of(message);
    }

    /**
     * Creates a ValidationException with multiple violation messages.
     * Use when checking all business rules upfront to report all failures at once.
     *
     * @param violations list of business rule violation descriptions
     */
    public ValidationException(List<String> violations) {
        super(
            "Request validation failed: " + violations.size() + " violation(s) found",
            HttpStatus.UNPROCESSABLE_ENTITY,
            "VALIDATION_FAILED"
        );
        this.violations = List.copyOf(violations);
    }

    /**
     * Returns all business rule violations found in this request.
     *
     * @return immutable list of violation messages
     */
    public List<String> getViolations() {
        return violations;
    }
}
