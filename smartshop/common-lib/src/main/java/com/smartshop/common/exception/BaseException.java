package com.smartshop.common.exception;

/**
 * BaseException - Parent exception type for domain errors.
 *
 * <h2>Purpose</h2>
 * A base domain exception makes it easier to build consistent error handlers and avoid leaking
 * low-level stack traces as public API output.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Semantic errors: encode business meaning instead of generic RuntimeException.</li>
 *   <li>Error code propagation: ties exception to stable API error codes.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Service-layer checks throw subclasses, while REST advice maps them to HTTP responses.
 *
 * @see ResourceNotFoundException
 * @author SmartShop Team
 */
public abstract class BaseException extends RuntimeException {

    private final String code;

    /**
     * Creates a new domain exception with a stable code.
     *
     * @param code machine-readable domain code
     * @param message human-readable failure explanation
     */
    protected BaseException(final String code, final String message) {
        super(message);
        this.code = code;
    }

    /**
     * Returns a stable machine-readable code for clients.
     *
     * @return domain error code
     */
    public String getCode() {
        return code;
    }
}
