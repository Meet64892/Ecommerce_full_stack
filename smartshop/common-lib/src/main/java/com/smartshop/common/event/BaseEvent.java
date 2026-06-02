package com.smartshop.common.event;

import java.time.Instant;
import java.util.UUID;

/**
 * BaseEvent - Shared metadata for Kafka event classes.
 *
 * <h2>Purpose</h2>
 * All asynchronous events need traceability: who emitted them, what type they are, and when they
 * were created. A base class keeps that metadata consistent and introduces the event-sourcing idea
 * that facts should be immutable and identifiable.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Event identity: eventId supports idempotency and duplicate detection.</li>
 *   <li>Correlation: correlationId links events back to the user request that produced them.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Order, inventory, and notification event classes extend BaseEvent before being serialized to Kafka.
 *
 * @see java.util.UUID
 * @author SmartShop Team
 */
public abstract class BaseEvent {
    private UUID eventId = UUID.randomUUID();
    private String eventType;
    private Instant timestamp = Instant.now();
    private String correlationId;

    /**
     * Default constructor is required by Jackson when Kafka messages are deserialized.
     */
    protected BaseEvent() {
    }

    /**
     * Creates an event with a semantic event type.
     *
     * @param eventType the stable event name such as order.created
     */
    protected BaseEvent(String eventType) {
        this.eventType = eventType;
    }

    /**
     * Returns the unique event id used for idempotent consumers.
     *
     * @return event UUID
     */
    public UUID getEventId() { return eventId; }

    /**
     * Updates the event id when deserializing existing messages.
     *
     * @param eventId event UUID
     */
    public void setEventId(UUID eventId) { this.eventId = eventId; }

    /**
     * Returns the business event type.
     *
     * @return event type text
     */
    public String getEventType() { return eventType; }

    /**
     * Sets the event type during construction or deserialization.
     *
     * @param eventType stable event type text
     */
    public void setEventType(String eventType) { this.eventType = eventType; }

    /**
     * Returns the event creation timestamp.
     *
     * @return creation timestamp
     */
    public Instant getTimestamp() { return timestamp; }

    /**
     * Sets the timestamp when replaying or deserializing an event.
     *
     * @param timestamp event creation timestamp
     */
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    /**
     * Returns the correlation id used to follow a request through HTTP and Kafka boundaries.
     *
     * @return correlation id, possibly null for externally produced events
     */
    public String getCorrelationId() { return correlationId; }

    /**
     * Sets the correlation id propagated from a gateway request.
     *
     * @param correlationId trace-friendly correlation identifier
     */
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
}
