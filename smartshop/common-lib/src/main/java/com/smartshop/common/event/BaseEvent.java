package com.smartshop.common.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

/**
 * BaseEvent - Root Class for All Kafka Domain Events in SmartShop
 *
 * <h2>Purpose</h2>
 * Every domain event published to Kafka (OrderCreated, InventoryReserved, UserRegistered)
 * extends this class to carry standard metadata. Without this base class, each event
 * would independently invent its own ID/timestamp fields — with different names and
 * formats — making event processing, logging, and tracing inconsistent.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Event Sourcing Intro: In event-driven systems, an "event" is an immutable
 *       record of something that HAPPENED (past tense). Events are facts. This is
 *       different from commands (requests for something to happen). By extending
 *       BaseEvent, all our events share traceability metadata.</li>
 *   <li>eventId (UUID): Every event gets a globally unique identifier. Consumers can
 *       use this to implement idempotency — if the same event is delivered twice
 *       (Kafka at-least-once guarantee), the consumer checks if eventId was already
 *       processed and skips duplicates.</li>
 *   <li>eventType: A string identifier of the event type (e.g., "ORDER_CREATED").
 *       Used for routing, filtering, and logging without deserializing the full payload.</li>
 *   <li>correlationId: When a user places an order, the entire Saga (inventory check,
 *       payment, confirmation) is one logical operation. The correlationId links all
 *       related events together, enabling distributed tracing across Kafka messages.
 *       This is analogous to HTTP's X-Correlation-ID header but for async messaging.</li>
 *   <li>@JsonTypeInfo: Embeds the Java class name in the JSON so deserialization
 *       can reconstruct the correct subclass without explicit type information from
 *       the consumer's configuration.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * OrderEventProducer publishes OrderCreatedEvent (extends BaseEvent) to Kafka.
 * InventoryService's Kafka consumer deserializes it, checks stock, then publishes
 * InventoryCheckedEvent. The correlationId threads through all these events,
 * allowing Zipkin to visualize the complete cross-service transaction.
 *
 * @see com.smartshop.order.event.OrderCreatedEvent
 * @author SmartShop Team
 */
@Getter
@SuperBuilder
@NoArgsConstructor
// Embeds "@class" property in JSON so Jackson knows which subclass to instantiate.
// Without this, deserializing a BaseEvent reference with OrderCreatedEvent data
// would fail — Jackson wouldn't know to instantiate OrderCreatedEvent.
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public abstract class BaseEvent {

    /**
     * Globally unique identifier for this specific event instance.
     * Generated with UUID v4 (random). Used for:
     *   1. Idempotency: consumers track processed eventIds to skip duplicates
     *   2. Debugging: find a specific event across all service logs
     *   3. Audit trail: every state change is traceable to an event
     */
    private String eventId;

    /**
     * The type of event, in SCREAMING_SNAKE_CASE (e.g., "ORDER_CREATED").
     * Set by each subclass. Used for:
     *   1. Log filtering: "show me all ORDER_CREATED events"
     *   2. Kafka topic routing in complex topologies
     *   3. Schema registry lookup in production systems
     */
    private String eventType;

    /**
     * UTC timestamp of when this event was created (not when it was published).
     * Instant is always UTC — critical for distributed systems where services
     * may run in different timezones.
     */
    private Instant occurredOn;

    /**
     * Links this event to a business transaction spanning multiple services.
     * Example: When user places an order:
     *   - OrderCreatedEvent.correlationId = "saga-abc123"
     *   - InventoryCheckedEvent.correlationId = "saga-abc123"
     *   - OrderConfirmedEvent.correlationId = "saga-abc123"
     * All three events are part of the same business operation and can be
     * correlated for debugging, auditing, and distributed tracing.
     */
    private String correlationId;

    /**
     * Initializes the standard event metadata that every subclass needs.
     * Called by subclass constructors via super().
     *
     * @param eventType     the type identifier for this event class
     * @param correlationId the saga/transaction correlation identifier
     */
    public void initializeEvent(String eventType, String correlationId) {
        // UUID.randomUUID() generates a version 4 UUID — guaranteed unique across
        // all JVMs and processes without coordination (no central sequence generator)
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.occurredOn = Instant.now();
        this.correlationId = correlationId;
    }
}
