package com.smartshop.common.event;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.UUID;

/**
 * BaseEvent - Common envelope every Kafka domain event extends.
 *
 * <h2>Purpose</h2>
 * Event-driven services exchange messages over Kafka. If each event invented its
 * own metadata fields, traceability and idempotency would be inconsistent. Every
 * SmartShop event extends this base so it always carries an {@code eventId},
 * an {@code eventType}, and a {@code timestamp}.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>eventId</b>: a globally unique id. Consumers store processed ids to
 *       achieve <i>idempotency</i> — re-delivered messages (Kafka guarantees
 *       at-least-once) can be safely ignored.</li>
 *   <li><b>eventType</b>: a discriminator string enabling polymorphic routing
 *       and a gentle introduction to <i>event sourcing</i> (the type log).</li>
 *   <li><b>timestamp</b>: when the event occurred, for ordering/auditing.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * order-service publishes subclasses (e.g. OrderCreatedEvent); inventory- and
 * notification-service consume them and use {@code eventId} to deduplicate.
 *
 * <p>Not a record because subclasses must extend it (records are final).
 *
 * @author SmartShop Team
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseEvent {

    /** Unique id for this specific event instance (used for idempotency). */
    private String eventId = UUID.randomUUID().toString();

    /** Discriminator describing the kind of event (e.g. "ORDER_CREATED"). */
    private String eventType;

    /** Wall-clock time the event was produced. */
    private Instant timestamp = Instant.now();

    /** Default constructor required by Jackson for deserialization. */
    protected BaseEvent() {
    }

    /**
     * @param eventType the discriminator value for this event subtype
     */
    protected BaseEvent(String eventType) {
        this.eventType = eventType;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
