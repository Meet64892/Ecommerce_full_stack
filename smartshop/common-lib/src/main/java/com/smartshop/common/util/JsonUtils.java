package com.smartshop.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * JsonUtils - Lightweight ObjectMapper wrapper.
 *
 * <h2>Purpose</h2>
 * Central JSON utility avoids repeated mapper setup and preserves deterministic serialization
 * behavior, especially useful in logging and Kafka debug tooling.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Single mapper configuration: consistent serialization across modules.</li>
 *   <li>Exception translation: wraps checked JSON exceptions into runtime errors.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Utility methods are reused by event publishers and diagnostic loggers.
 *
 * @see DateUtils
 * @author SmartShop Team
 */
public final class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private JsonUtils() {
        // Utility class pattern.
    }

    /**
     * Serializes an object to JSON.
     *
     * @param value object to serialize
     * @return JSON string representation
     * @throws IllegalStateException when Jackson cannot serialize value
     */
    public static String toJson(final Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize value to JSON", ex);
        }
    }
}
