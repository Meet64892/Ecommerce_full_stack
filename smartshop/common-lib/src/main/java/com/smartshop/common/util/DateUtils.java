package com.smartshop.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * DateUtils - Centralized Date/Time Conversion Utilities
 *
 * <h2>Purpose</h2>
 * Date/time handling is a frequent source of bugs in distributed systems:
 * timezone mismatches, format inconsistencies, and epoch vs ISO-8601 confusion.
 * Centralizing these conversions in one utility class ensures every service
 * uses the same format and timezone conventions.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Instant vs LocalDateTime: Instant is a point in time (like a Unix timestamp)
 *       with no timezone concept — always UTC. LocalDateTime is a date+time without
 *       timezone information. In distributed systems, always prefer Instant for
 *       storage and transmission; convert to LocalDateTime only for display.</li>
 *   <li>ZoneOffset.UTC vs ZoneId.of("UTC"): Both refer to UTC but have different
 *       types. ZoneOffset.UTC is preferred for Instant conversions.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
public final class DateUtils {

    // ISO-8601 format with milliseconds: "2024-01-15T10:30:45.123Z"
    // The 'Z' suffix means UTC (Zulu time) — unambiguous for API clients
    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .withZone(ZoneOffset.UTC);

    // Private constructor prevents instantiation — this is a utility class with
    // only static methods. No reason to ever create an instance.
    private DateUtils() {
        throw new UnsupportedOperationException("DateUtils is a utility class and cannot be instantiated");
    }

    /**
     * Formats an Instant as an ISO-8601 string for API responses and logging.
     *
     * @param instant the point in time to format
     * @return ISO-8601 formatted string like "2024-01-15T10:30:45.123Z"
     */
    public static String formatInstant(Instant instant) {
        if (instant == null) {
            return null;
        }
        return ISO_FORMATTER.format(instant);
    }

    /**
     * Converts LocalDateTime (assumed UTC) to Instant.
     * Used when reading from databases that store LocalDateTime without timezone.
     * WARNING: Only call this if you're certain the LocalDateTime is in UTC.
     *
     * @param localDateTime the UTC local date time from the database
     * @return the equivalent Instant in UTC
     */
    public static Instant toInstant(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        // atOffset creates a ZonedDateTime so toInstant() can resolve the timezone
        return localDateTime.toInstant(ZoneOffset.UTC);
    }

    /**
     * Converts an Instant to LocalDateTime in the specified timezone.
     * Used only for display purposes — never for storage.
     *
     * @param instant  the UTC instant to convert
     * @param zoneId   the target timezone (e.g., "America/New_York", "Europe/London")
     * @return the LocalDateTime in the target timezone
     */
    public static LocalDateTime toLocalDateTime(Instant instant, String zoneId) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZoneId.of(zoneId));
    }
}
