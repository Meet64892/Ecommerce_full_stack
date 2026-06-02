package com.smartshop.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * BaseException - Root of the SmartShop Custom Exception Hierarchy
 *
 * <h2>Purpose</h2>
 * By creating a common base exception, we allow GlobalExceptionHandler to
 * catch ALL SmartShop-specific exceptions with a single catch block:
 * {@code catch (BaseException e)}. Without this hierarchy, we'd need separate
 * handler methods for every exception type, leading to massive handler classes.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Exception Hierarchy: Java exceptions form an inheritance tree.
 *       By extending RuntimeException (unchecked), callers don't need to
 *       declare throws clauses — they propagate up automatically.</li>
 *   <li>Checked vs Unchecked: Checked exceptions (extending Exception) force
 *       callers to handle them. Unchecked exceptions (extending RuntimeException)
 *       are for programmer errors or business rule violations — Spring's convention
 *       is to use unchecked exceptions for service-layer business errors.</li>
 *   <li>HTTP Status Embedding: Embedding the intended HTTP status code in the
 *       exception means GlobalExceptionHandler doesn't need a mapping table —
 *       the exception itself knows what status code it should produce.</li>
 *   <li>Error Code: A stable machine-readable identifier for this error type,
 *       usable by clients for conditional error handling logic.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Service layer throws subclasses: UserNotFoundException, StockInsufficientException, etc.
 * These propagate up to GlobalExceptionHandler which catches BaseException and
 * builds an ErrorResponse using the embedded status and errorCode.
 *
 * @see ResourceNotFoundException
 * @see ValidationException
 * @author SmartShop Team
 */
@Getter
public abstract class BaseException extends RuntimeException {

    /**
     * The HTTP status code this exception should produce when caught by
     * GlobalExceptionHandler. Embedding it here removes the need for a
     * separate exception-to-status mapping in the handler.
     */
    private final HttpStatus httpStatus;

    /**
     * Machine-readable error code for API clients. Must be stable across
     * releases — once you publish an error code, clients will depend on it.
     * Convention: SCREAMING_SNAKE_CASE (e.g., "USER_NOT_FOUND").
     */
    private final String errorCode;

    /**
     * Creates a new BaseException with a human-readable message, HTTP status, and error code.
     *
     * @param message   human-readable description of what went wrong
     * @param httpStatus the HTTP status code to return to the client
     * @param errorCode  the machine-readable error identifier
     */
    protected BaseException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    /**
     * Creates a new BaseException that wraps another exception (exception chaining).
     * Exception chaining preserves the original stack trace, which is critical for
     * debugging — without it, you lose context about what really went wrong.
     *
     * @param message   human-readable description of what went wrong
     * @param cause     the underlying exception that triggered this one
     * @param httpStatus the HTTP status code to return
     * @param errorCode  the machine-readable error identifier
     */
    protected BaseException(String message, Throwable cause, HttpStatus httpStatus, String errorCode) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}
