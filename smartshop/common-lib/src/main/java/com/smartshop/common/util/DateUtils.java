package com.smartshop.common.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * DateUtils - Small, dependency-free date/time helpers.
 *
 * <h2>Purpose</h2>
 * Centralizes the canonical timestamp format used in logs and API payloads so
 * every service formats time identically (critical when correlating distributed
 * traces).
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Always work in UTC to avoid timezone ambiguity across services/regions.</li>
 *   <li>ISO-8601 is the interoperable, lexicographically sortable standard.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Used wherever a human-readable timestamp string is needed.
 *
 * @author SmartShop Team
 */
public final class DateUtils {

    /** ISO-8601 formatter pinned to UTC for consistent, sortable output. */
    private static final DateTimeFormatter ISO_UTC =
            DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    /** Private constructor: this is a static utility holder, never instantiated. */
    private DateUtils() {
    }

    /**
     * Formats an instant as an ISO-8601 UTC string.
     *
     * @param instant the moment to format (must not be null)
     * @return e.g. {@code 2026-06-02T11:00:00Z}
     */
    public static String formatIso(Instant instant) {
        return ISO_UTC.format(instant);
    }

    /**
     * @return the current moment formatted as an ISO-8601 UTC string
     */
    public static String nowIso() {
        return formatIso(Instant.now());
    }
}
