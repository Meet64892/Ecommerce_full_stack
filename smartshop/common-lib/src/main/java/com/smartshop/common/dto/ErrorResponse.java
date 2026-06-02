package com.smartshop.common.dto;

import java.time.Instant;
import java.util.List;

/**
 * ErrorResponse - Standard error envelope for failed HTTP requests.
 *
 * <h2>Purpose</h2>
 * Error responses need to be predictable because user interfaces, API consumers, and logs all
 * parse them. This record keeps machine-readable codes separate from human-readable messages.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Standardization: Every service reports validation and domain errors the same way.</li>
 *   <li>Details list: Multiple field errors can travel in one response.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Global exception handlers build ErrorResponse objects whenever controllers throw exceptions.
 *
 * @see com.smartshop.common.exception.BaseException
 * @author SmartShop Team
 */
public record ErrorResponse(String code, String message, List<String> details, Instant timestamp) {
    /**
     * Builds a single-message error for domain exceptions where field-level details are unavailable.
     *
     * @param code a stable machine-readable error code
     * @param message a safe human-readable message
     * @return a timestamped error response
     */
    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, List.of(), Instant.now());
    }

    /**
     * Builds a detailed error for validation failures.
     *
     * @param code a stable machine-readable error code
     * @param message a summary message
     * @param details individual validation or diagnostic messages
     * @return a timestamped error response with all details preserved
     */
    public static ErrorResponse of(String code, String message, List<String> details) {
        return new ErrorResponse(code, message, details, Instant.now());
    }
}
