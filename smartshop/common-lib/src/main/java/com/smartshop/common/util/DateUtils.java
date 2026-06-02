package com.smartshop.common.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * DateUtils - Shared UTC date formatting helpers.
 *
 * <h2>Purpose</h2>
 * A single utility avoids scattered date formatting logic and enforces UTC output, which is
 * critical when logs and events are correlated across distributed nodes.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>UTC normalization: avoids timezone drift bugs.</li>
 *   <li>ISO-8601: interoperable timestamp format for APIs and events.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Event and logging code can use this utility when explicit formatting is needed.
 *
 * @see JsonUtils
 * @author SmartShop Team
 */
public final class DateUtils {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    private DateUtils() {
        // Utility class: private constructor prevents accidental instantiation.
    }

    /**
     * Formats an Instant as UTC ISO-8601 string.
     *
     * @param instant timestamp to format
     * @return formatted UTC string
     */
    public static String toIsoUtc(final Instant instant) {
        return ISO_FORMATTER.format(instant);
    }
}
