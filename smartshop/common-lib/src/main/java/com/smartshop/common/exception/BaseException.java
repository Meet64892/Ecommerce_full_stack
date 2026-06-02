package com.smartshop.common.exception;

/**
 * BaseException - Parent class for SmartShop domain exceptions.
 *
 * <h2>Purpose</h2>
 * Domain exceptions should carry stable error codes as well as messages. A shared base type lets
 * exception handlers treat known business failures differently from unexpected infrastructure failures.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Error code: A stable identifier clients can branch on safely.</li>
 *   <li>Unchecked exception: Services can throw domain failures without cluttering every method signature.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Service methods throw subclasses and GlobalExceptionHandler converts them to ErrorResponse JSON.
 *
 * @see ResourceNotFoundException
 * @author SmartShop Team
 */
public abstract class BaseException extends RuntimeException {
    private final String code;

    /**
     * Captures the error code and message at the point the domain rule fails.
     *
     * @param code machine-readable error code
     * @param message human-readable message safe for API clients
     */
    protected BaseException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Returns the stable code used in API responses and logs.
     *
     * @return machine-readable error code
     */
    public String getCode() {
        return code;
    }
}
