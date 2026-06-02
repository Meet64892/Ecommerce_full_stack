package com.smartshop.common.dto;

import java.time.Instant;

/**
 * ApiResponse - Generic success envelope for API payloads.
 *
 * <h2>Purpose</h2>
 * A shared response wrapper keeps API contracts consistent across independent services.
 * This consistency improves front-end integration and cross-team governance.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>DRY contract: common shape avoids duplicate serializer definitions.</li>
 *   <li>Envelope pattern: transports metadata alongside actual data payload.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Controllers from every service can return this wrapper to standardize clients' parsing logic.
 *
 * @see ErrorResponse
 * @author SmartShop Team
 */
public record ApiResponse<T>(boolean success, T data, String message, Instant timestamp) {

    /**
     * Factory method for successful responses with current timestamp.
     *
     * @param data domain payload being returned
     * @param message human-readable status message
     * @return immutable API response instance
     */
    public static <T> ApiResponse<T> success(final T data, final String message) {
        return new ApiResponse<>(true, data, message, Instant.now());
    }
}
