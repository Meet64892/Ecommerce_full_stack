package com.smartshop.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * ApiResponse - Generic Wrapper for ALL HTTP Responses in SmartShop
 *
 * <h2>Purpose</h2>
 * Every endpoint across every microservice returns this envelope instead of raw
 * domain objects. This gives clients (mobile apps, frontends, other services) a
 * predictable response shape regardless of which service they called.
 * Without this, one service might return a bare Product JSON while another wraps
 * it in a "result" field — forcing clients to write different parsing logic per service.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Generic Type {@code <T>}: The {@code data} field can hold any type — a single
 *       User, a list of Products, a page of Orders. Java generics let us have one
 *       class serve all these cases without losing type safety.</li>
 *   <li>@JsonInclude(NON_NULL): Fields with null values are omitted from JSON output.
 *       A success response won't include "errorCode": null, keeping payloads clean.</li>
 *   <li>Builder Pattern (Lombok @Builder): Instead of multiple constructors, callers use
 *       ApiResponse.success(data) factory methods for readability.</li>
 *   <li>Immutability: Once constructed, this object cannot be modified. This is
 *       thread-safe by design — no setter methods means no accidental mutation.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Controllers return {@code ResponseEntity<ApiResponse<T>>}. GlobalExceptionHandler
 * also wraps errors in this format (though ErrorResponse has a different shape).
 *
 * @param <T> the type of the payload data
 * @see ErrorResponse
 * @author SmartShop Team
 */
@Getter
@Builder
// Only serialize non-null fields — prevents ugly "data": null in error responses
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * Whether the operation succeeded. Clients can check this boolean before
     * attempting to access the data field, avoiding null pointer issues.
     */
    private final boolean success;

    /**
     * The actual response payload. Null for error responses. Using a generic
     * type parameter T means this can hold User, Product, List<Order>, etc.
     * without creating separate response classes for each domain object.
     */
    private final T data;

    /**
     * Human-readable message for the client. Examples:
     * "User registered successfully", "Product not found", "Order confirmed"
     */
    private final String message;

    /**
     * ISO-8601 timestamp of when this response was generated.
     * Useful for debugging, logging, and caching decisions.
     * Instant is used instead of LocalDateTime because Instant is timezone-agnostic
     * (always UTC), preventing confusion in distributed systems spanning multiple zones.
     */
    private final Instant timestamp;

    /**
     * Creates a successful response with data and a default message.
     * This is the most common factory method — used by controllers returning data.
     *
     * @param data the response payload to wrap
     * @param <T>  the type of the payload
     * @return a new ApiResponse with success=true and the current timestamp
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message("Operation successful")
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Creates a successful response with a custom message.
     * Used when the message adds business context, e.g., "Order #123 placed successfully".
     *
     * @param data    the response payload
     * @param message a human-readable success message
     * @param <T>     the type of the payload
     * @return a new ApiResponse with success=true
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Creates an error response without a data payload.
     * Used by GlobalExceptionHandler to wrap exceptions into the standard format.
     *
     * @param message a description of what went wrong
     * @param <T>     the type parameter (null in error case, but needed for type inference)
     * @return a new ApiResponse with success=false and no data
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }
}
