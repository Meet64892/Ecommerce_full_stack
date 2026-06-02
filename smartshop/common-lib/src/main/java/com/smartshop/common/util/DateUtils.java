package com.smartshop.common.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * DateUtils - Shared UTC date formatting helpers.
 *
 * <h2>Purpose</h2>
 * Microservices should agree on timestamp formats so logs and events can be compared without
 * timezone confusion. Keeping this helper in common-lib avoids subtle formatting drift.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>UTC: Server-side timestamps should be stored and exchanged in UTC.</li>
 *   <li>ISO-8601: A portable timestamp format understood by most tools.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Events and error responses can call DateUtils when they need stable human-readable timestamps.
 *
 * @see java.time.Instant
 * @author SmartShop Team
 */
public final class DateUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC);

    private DateUtils() {
        // Utility classes are not instantiated because all behavior is stateless.
    }

    /**
     * Formats an Instant as an ISO-8601 UTC string.
     *
     * @param instant the point in time to format
     * @return UTC timestamp text, or the current instant if the input is null
     */
    public static String toUtcString(Instant instant) {
        return FORMATTER.format(instant == null ? Instant.now() : instant);
    }
}
