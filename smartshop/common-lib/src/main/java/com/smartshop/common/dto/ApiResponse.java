package com.smartshop.common.dto;

import java.time.Instant;

/**
 * ApiResponse - Standard success envelope for HTTP responses.
 *
 * <h2>Purpose</h2>
 * This record gives every service the same outward response shape so clients do not learn
 * different conventions for each bounded context. A consistent envelope also makes logging,
 * API documentation, and UI error handling simpler.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>DRY principle: Shared response code avoids copy-paste across services.</li>
 *   <li>Java records: Records are immutable data carriers introduced in Java 16.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Controllers create ApiResponse instances and Spring MVC serializes them to JSON for API clients.
 *
 * @see ErrorResponse
 * @author SmartShop Team
 */
public record ApiResponse<T>(boolean success, T data, String message, Instant timestamp) {
    /**
     * Creates a successful response with the current timestamp so clients can correlate freshness.
     *
     * @param data the payload returned by a controller
     * @param message a human-readable success message
     * @return an immutable successful response envelope
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, Instant.now());
    }

    /**
     * Creates a failure-style envelope for cases where an endpoint intentionally returns a 200 with no data.
     *
     * @param message the explanatory message to show to callers
     * @return an immutable response with success set to false
     */
    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(false, null, message, Instant.now());
    }
}
