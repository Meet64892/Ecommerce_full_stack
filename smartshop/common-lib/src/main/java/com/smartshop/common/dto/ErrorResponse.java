package com.smartshop.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

/**
 * ErrorResponse - Standardized Error Payload for All HTTP Error Responses
 *
 * <h2>Purpose</h2>
 * When an exception occurs (validation failure, not found, unauthorized), the
 * GlobalExceptionHandler in each service catches it and returns this structured
 * object. Without a standard error format, API clients would receive inconsistent
 * error shapes — sometimes a Spring Boot default white-label error, sometimes a
 * custom message — making it impossible to write reliable error handling code.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Error Code: A machine-readable string like "USER_NOT_FOUND" or
 *       "VALIDATION_FAILED". Clients can switch on this code without parsing
 *       human-readable messages (which might change).</li>
 *   <li>Details List: For validation errors, multiple fields may fail simultaneously.
 *       This list carries each field's specific violation message.</li>
 *   <li>HTTP Status: Included in the body (in addition to the HTTP status line)
 *       for clients that log raw response bodies without the status code.</li>
 *   <li>Path: The request URI that triggered this error — essential for
 *       debugging in logs where multiple concurrent requests are interleaved.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * GlobalExceptionHandler catches all exceptions and maps them to ErrorResponse.
 * The API Gateway forwards these unchanged to the client. Clients should check
 * the HTTP status code first, then inspect errorCode for specific handling.
 *
 * @see com.smartshop.common.exception.BaseException
 * @author SmartShop Team
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * HTTP status code (e.g., 400, 404, 500). Duplicated in the body for
     * clients that process response bodies in bulk logs without status codes.
     */
    private final int status;

    /**
     * Machine-readable error code for programmatic client handling.
     * Convention: SCREAMING_SNAKE_CASE, e.g., "USER_NOT_FOUND", "STOCK_INSUFFICIENT".
     * These codes are stable API contracts — never rename them once published.
     */
    private final String errorCode;

    /**
     * Human-readable error description for developer debugging.
     * This message may be shown to users for 4xx errors but should be
     * generic for 5xx errors to avoid leaking implementation details.
     */
    private final String message;

    /**
     * Detailed validation errors for 400 Bad Request responses.
     * Example: ["email: must be a valid email address", "age: must be >= 18"]
     * Null for non-validation errors (omitted from JSON via @JsonInclude).
     */
    private final List<String> details;

    /**
     * The request path that caused this error, e.g., "/api/users/999".
     * Critical for correlating errors in distributed logs across services.
     */
    private final String path;

    /**
     * Timestamp of when the error occurred.
     * Use Instant (UTC) to avoid timezone confusion in distributed environments.
     */
    private final Instant timestamp;

    /**
     * Convenience factory for simple error responses without field-level details.
     *
     * @param status    the HTTP status code
     * @param errorCode the machine-readable error identifier
     * @param message   the human-readable error description
     * @param path      the request URI that triggered this error
     * @return a fully constructed ErrorResponse
     */
    public static ErrorResponse of(int status, String errorCode, String message, String path) {
        return ErrorResponse.builder()
                .status(status)
                .errorCode(errorCode)
                .message(message)
                .path(path)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Factory for validation errors with multiple field-level messages.
     *
     * @param path    the request URI
     * @param details list of "fieldName: violation message" strings
     * @return an ErrorResponse with status 400 and validation details
     */
    public static ErrorResponse validationError(String path, List<String> details) {
        return ErrorResponse.builder()
                .status(400)
                .errorCode("VALIDATION_FAILED")
                .message("Request validation failed. Check the details field for specific errors.")
                .details(details)
                .path(path)
                .timestamp(Instant.now())
                .build();
    }
}
