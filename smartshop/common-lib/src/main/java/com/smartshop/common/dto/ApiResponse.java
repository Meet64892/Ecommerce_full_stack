package com.smartshop.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * ApiResponse - Uniform success/response envelope returned by every service.
 *
 * <h2>Purpose</h2>
 * Without a shared response shape, each microservice would invent its own JSON
 * structure, forcing clients (and the API gateway) to special-case every
 * endpoint. This generic wrapper guarantees that <em>every</em> successful
 * response across the platform looks the same: {@code {success, data, message,
 * timestamp}}.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Generics ({@code <T>}): the {@code data} payload can be any type
 *       (a UserDto, a list of products, ...) while keeping compile-time safety.</li>
 *   <li>Java record: an immutable, value-based carrier (Java 16+). The compiler
 *       generates the constructor, accessors, {@code equals}/{@code hashCode}
 *       and {@code toString}, so there is no boilerplate to maintain.</li>
 *   <li>{@code @JsonInclude(NON_NULL)}: null fields are omitted from JSON,
 *       keeping responses compact.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Controllers in every service wrap their results with {@link #ok} (or
 * {@link #of}) before returning. The gateway and front-end can therefore rely
 * on a single contract for unwrapping data.
 *
 * @param <T> the type of the {@code data} payload
 * @see ErrorResponse
 * @author SmartShop Team
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        String message,
        Instant timestamp
) {

    /**
     * Builds a successful response with a default "OK" message.
     *
     * @param data the payload to return to the caller
     * @param <T>  the payload type
     * @return an {@code ApiResponse} flagged successful with the current timestamp
     */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, "OK", Instant.now());
    }

    /**
     * Builds a successful response with a custom human-readable message.
     *
     * @param data    the payload to return
     * @param message a short message describing the outcome
     * @param <T>     the payload type
     * @return a successful {@code ApiResponse}
     */
    public static <T> ApiResponse<T> of(T data, String message) {
        return new ApiResponse<>(true, data, message, Instant.now());
    }
}
