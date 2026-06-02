package com.smartshop.common.dto;

import java.time.Instant;
import java.util.List;

/**
 * ErrorResponse - Standardized error payload.
 *
 * <h2>Purpose</h2>
 * Error payload consistency allows API clients and observability tools to interpret failures
 * uniformly regardless of which service produced the error.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Error code taxonomy: machine-readable codes improve automation and retries.</li>
 *   <li>Detail list: captures field-level validation problems.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Global exception handlers in each microservice transform runtime exceptions into this shape.
 *
 * @see com.smartshop.common.exception.BaseException
 * @author SmartShop Team
 */
public record ErrorResponse(String code, String message, List<String> details, Instant timestamp) {

    /**
     * Builds a standardized error response with current timestamp.
     *
     * @param code machine-readable error code
     * @param message user-facing summary
     * @param details optional field or domain detail entries
     * @return immutable error DTO
     */
    public static ErrorResponse of(final String code, final String message, final List<String> details) {
        return new ErrorResponse(code, message, details, Instant.now());
    }
}
