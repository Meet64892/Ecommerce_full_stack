package com.smartshop.common.exception;

/**
 * BaseException - Root of the SmartShop custom exception hierarchy.
 *
 * <h2>Purpose</h2>
 * Every domain-specific exception in the platform extends this class so that
 * cross-cutting handlers can catch a single supertype and still read a stable
 * {@code errorCode}. This keeps the exception-to-HTTP mapping logic small and
 * predictable.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Unchecked (extends {@link RuntimeException}): we deliberately use
 *       unchecked exceptions for business errors so service code is not forced
 *       to declare {@code throws} everywhere; Spring's transaction layer also
 *       rolls back on {@code RuntimeException} by default.</li>
 *   <li>{@code errorCode}: a stable, machine-readable code surfaced in
 *       {@code ErrorResponse.code} independent of the human message.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Thrown by service/repository layers; caught by each service's
 * {@code GlobalExceptionHandler} which reads {@link #getErrorCode()} to build a
 * consistent error body.
 *
 * @see ResourceNotFoundException
 * @see ValidationException
 * @author SmartShop Team
 */
public abstract class BaseException extends RuntimeException {

    /** Stable, machine-readable identifier for this error category. */
    private final String errorCode;

    /**
     * Creates a base exception.
     *
     * @param errorCode stable code (e.g. {@code RESOURCE_NOT_FOUND})
     * @param message   human-readable detail message
     */
    protected BaseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * @return the stable machine-readable error code for mapping to responses
     */
    public String getErrorCode() {
        return errorCode;
    }
}
