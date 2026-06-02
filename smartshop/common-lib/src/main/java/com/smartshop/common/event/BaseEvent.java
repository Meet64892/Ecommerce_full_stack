package com.smartshop.common.event;

import java.time.Instant;
import java.util.UUID;

/**
 * BaseEvent - Parent event contract for Kafka messages.
 *
 * <h2>Purpose</h2>
 * A shared base event ensures every emitted message contains identity and timestamp metadata,
 * enabling traceability, replay analysis, and event-sourcing-friendly audit trails.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Event identity: UUID prevents ambiguity across retries.</li>
 *   <li>Event typing: explicit eventType simplifies polymorphic consumers.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Order, inventory, and user domain events extend this class before being published to Kafka.
 *
 * @see com.smartshop.common.util.DateUtils
 * @author SmartShop Team
 */
public abstract class BaseEvent {

    private final String eventId;
    private final String eventType;
    private final Instant timestamp;

    /**
     * Default constructor for serialization frameworks.
     *
     * @param none no parameters required
     */
    protected BaseEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = "UNKNOWN";
        this.timestamp = Instant.now();
    }

    /**
     * Creates a base event with auto-generated ID and current timestamp.
     *
     * @param eventType semantic event type name
     */
    protected BaseEvent(final String eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.timestamp = Instant.now();
    }

    /**
     * Returns unique event identifier.
     *
     * @return event UUID string
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Returns semantic event type.
     *
     * @return event type name
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Returns event creation timestamp.
     *
     * @return event instant
     */
    public Instant getTimestamp() {
        return timestamp;
    }
}
