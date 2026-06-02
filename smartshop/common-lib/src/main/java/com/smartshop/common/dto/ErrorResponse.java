package com.smartshop.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * ErrorResponse - Standardized error body returned by every {@code @RestControllerAdvice}.
 *
 * <h2>Purpose</h2>
 * Clients must be able to handle failures consistently. By returning the same
 * error structure everywhere ({@code {code, message, details, timestamp}}), a
 * front-end can render errors generically and support engineers can correlate
 * problems across services.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@code code}: a stable, machine-readable identifier (e.g.
 *       {@code USER_NOT_FOUND}) that does not change when we reword the human
 *       message.</li>
 *   <li>{@code details}: optional list of field-level problems, populated for
 *       validation failures so the UI can highlight individual inputs.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Global exception handlers in each service convert thrown exceptions into this
 * shape, so HTTP error payloads are uniform platform-wide.
 *
 * @see ApiResponse
 * @author SmartShop Team
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String code,
        String message,
        List<String> details,
        Instant timestamp
) {

    /**
     * Convenience factory for an error without field-level details.
     *
     * @param code    stable machine-readable error code
     * @param message human-readable explanation
     * @return an {@code ErrorResponse} stamped with the current time
     */
    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, null, Instant.now());
    }

    /**
     * Factory for an error that carries field-level validation details.
     *
     * @param code    stable machine-readable error code
     * @param message human-readable explanation
     * @param details per-field messages (e.g. "email: must not be blank")
     * @return an {@code ErrorResponse} including the detail list
     */
    public static ErrorResponse of(String code, String message, List<String> details) {
        return new ErrorResponse(code, message, details, Instant.now());
    }
}
