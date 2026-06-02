package com.smartshop.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * JsonUtils - Thread-Safe ObjectMapper Wrapper for JSON Operations
 *
 * <h2>Purpose</h2>
 * Jackson's ObjectMapper is expensive to create (it loads schema caches) but is
 * thread-safe once created. This utility provides a single pre-configured instance
 * shared across the application. It also adds proper handling for Java 8+ date/time
 * types (Instant, LocalDateTime) which Jackson doesn't handle by default.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>ObjectMapper thread safety: ObjectMapper is safe for concurrent use after
 *       configuration. Never create one per request — it's ~1000x more expensive.</li>
 *   <li>JavaTimeModule: Jackson can't serialize Instant by default — it throws
 *       "InvalidDefinitionException: Java 8 date/time type not supported".
 *       JavaTimeModule adds serializers for all java.time types.</li>
 *   <li>WRITE_DATES_AS_TIMESTAMPS=false: Without this, Instant serializes as
 *       [1705312245, 123000000] (epoch array). With it, you get "2024-01-15T10:30:45.123Z".</li>
 * </ul>
 *
 * @author SmartShop Team
 */
public final class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    // Singleton ObjectMapper — configured once at class load time (thread-safe)
    private static final ObjectMapper MAPPER = createObjectMapper();

    private JsonUtils() {
        throw new UnsupportedOperationException("JsonUtils is a utility class");
    }

    /**
     * Creates and configures the shared ObjectMapper instance.
     * Called only once when the class is first loaded by the JVM.
     *
     * @return a fully configured ObjectMapper
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Register JavaTimeModule to handle Instant, LocalDateTime, ZonedDateTime, etc.
        // Without this, serializing an Instant field throws InvalidDefinitionException
        mapper.registerModule(new JavaTimeModule());

        // Serialize dates as ISO-8601 strings, not as epoch timestamps
        // "2024-01-15T10:30:45.123Z" is human-readable; [1705312245, 123000000] is not
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }

    /**
     * Serializes an object to a JSON string.
     * Returns empty Optional on serialization failure (logged as error).
     * We return Optional instead of throwing to allow callers to handle
     * serialization failures gracefully (e.g., log and skip vs crash).
     *
     * @param object the object to serialize
     * @return Optional containing the JSON string, or empty if serialization failed
     */
    public static Optional<String> toJson(Object object) {
        try {
            return Optional.of(MAPPER.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object of type {} to JSON: {}",
                    object.getClass().getSimpleName(), e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Deserializes a JSON string to the specified type.
     * Returns empty Optional on failure rather than throwing, allowing
     * callers (especially Kafka consumers) to handle malformed messages gracefully.
     *
     * @param json      the JSON string to deserialize
     * @param valueType the target class
     * @param <T>       the target type
     * @return Optional containing the deserialized object, or empty if failed
     */
    public static <T> Optional<T> fromJson(String json, Class<T> valueType) {
        try {
            return Optional.of(MAPPER.readValue(json, valueType));
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON to {}: {}", valueType.getSimpleName(), e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Deserializes a JSON string to a generic type (e.g., List<User>).
     * Use TypeReference to preserve generic type information at runtime:
     * {@code JsonUtils.fromJson(json, new TypeReference<List<UserDto>>() {})}
     * Java's type erasure removes generic info at runtime; TypeReference uses
     * an anonymous subclass trick to preserve it.
     *
     * @param json          the JSON string to deserialize
     * @param typeReference the type reference preserving generic type info
     * @param <T>           the target type
     * @return Optional containing the deserialized object, or empty if failed
     */
    public static <T> Optional<T> fromJson(String json, TypeReference<T> typeReference) {
        try {
            return Optional.of(MAPPER.readValue(json, typeReference));
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Returns the shared ObjectMapper for advanced use cases.
     * Only use when the helper methods above don't cover your need.
     *
     * @return the shared ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        return MAPPER;
    }
}
