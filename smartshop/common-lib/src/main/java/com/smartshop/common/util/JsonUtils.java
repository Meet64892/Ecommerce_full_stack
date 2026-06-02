package com.smartshop.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * JsonUtils - Thin wrapper around a pre-configured Jackson {@link ObjectMapper}.
 *
 * <h2>Purpose</h2>
 * Constructing an {@code ObjectMapper} correctly (Java-time support, lenient
 * unknown-property handling) is easy to get wrong and expensive to repeat.
 * Building it once here guarantees every service serializes Kafka events and
 * logs identically.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@link JavaTimeModule}: teaches Jackson to handle {@code Instant} /
 *       {@code LocalDateTime} (otherwise it throws on java.time types).</li>
 *   <li>{@code FAIL_ON_UNKNOWN_PROPERTIES=false}: forward-compatibility — a
 *       newer producer can add fields without breaking older consumers.</li>
 *   <li>{@code ObjectMapper} is thread-safe once configured, so a single static
 *       instance is the recommended pattern.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Used by Kafka (de)serializers and anywhere ad-hoc JSON conversion is needed.
 *
 * @author SmartShop Team
 */
public final class JsonUtils {

    /** Shared, thread-safe, pre-configured mapper. */
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private JsonUtils() {
    }

    /**
     * Serializes any object to a JSON string.
     *
     * @param value the object to serialize
     * @return its JSON representation
     * @throws IllegalStateException if the object cannot be serialized
     */
    public static String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            // Wrap the checked Jackson exception so callers aren't forced to
            // handle it for what is almost always a programming error.
            throw new IllegalStateException("Failed to serialize object to JSON", e);
        }
    }

    /**
     * Deserializes a JSON string into the requested type.
     *
     * @param json the JSON text
     * @param type the target class
     * @param <T>  the target type
     * @return the deserialized instance
     * @throws IllegalStateException if the JSON cannot be parsed into {@code type}
     */
    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize JSON to " + type.getSimpleName(), e);
        }
    }

    /**
     * @return the shared configured mapper (e.g. to register with Spring beans)
     */
    public static ObjectMapper mapper() {
        return MAPPER;
    }
}
