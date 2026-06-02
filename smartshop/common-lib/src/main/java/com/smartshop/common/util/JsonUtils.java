package com.smartshop.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * JsonUtils - Small ObjectMapper wrapper for shared serialization needs.
 *
 * <h2>Purpose</h2>
 * Kafka events and diagnostic logs sometimes need explicit JSON conversion outside Spring MVC.
 * This wrapper centralizes ObjectMapper setup so Java time values serialize consistently.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>ObjectMapper: Jackson's configurable JSON serializer/deserializer.</li>
 *   <li>JavaTimeModule: Adds support for Instant, LocalDate, and other java.time types.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Services can use this utility when not already inside Spring's HTTP message conversion layer.
 *
 * @see ObjectMapper
 * @author SmartShop Team
 */
public final class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    private JsonUtils() {
        // Utility classes are intentionally non-instantiable.
    }

    /**
     * Serializes an object to JSON and wraps checked Jackson failures in an IllegalArgumentException.
     *
     * @param value the object to serialize
     * @return JSON string representing the object
     * @throws IllegalArgumentException when Jackson cannot serialize the value
     */
    public static String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Unable to serialize value to JSON", ex);
        }
    }

    /**
     * Deserializes JSON to a target class.
     *
     * @param json the JSON document to parse
     * @param type target Java class
     * @return parsed object instance
     * @throws IllegalArgumentException when JSON does not match the target type
     */
    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Unable to deserialize JSON", ex);
        }
    }
}
