package com.smartshop.user.event;

import com.smartshop.common.event.BaseEvent;

import java.util.UUID;

/**
 * UserRegisteredEvent - Kafka event emitted after a user account is created.
 *
 * <h2>Purpose</h2>
 * Registration should not wait for welcome email delivery, so user-service publishes an event after the account is
 * persisted. Notification-service consumes the event and handles the side effect asynchronously.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Event-driven side effect: Email is triggered without a direct HTTP call.</li>
 *   <li>Traceability: BaseEvent adds eventId and timestamp for observability and idempotency.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * UserServiceImpl publishes this event and UserEventConsumer in notification-service sends the welcome email.
 *
 * @see com.smartshop.user.kafka.UserEventProducer
 * @author SmartShop Team
 */
public class UserRegisteredEvent extends BaseEvent {
    private UUID userId;
    private String email;
    private String firstName;

    /** Required by Jackson for Kafka deserialization. */
    public UserRegisteredEvent() { super("user.registered"); }

    /**
     * Creates a registration event.
     *
     * @param userId created user id
     * @param email recipient email
     * @param firstName recipient first name
     */
    public UserRegisteredEvent(UUID userId, String email, String firstName) {
        super("user.registered");
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
    }

    /** @return created user id */ public UUID getUserId() { return userId; }
    /** @param userId created user id */ public void setUserId(UUID userId) { this.userId = userId; }
    /** @return user email */ public String getEmail() { return email; }
    /** @param email user email */ public void setEmail(String email) { this.email = email; }
    /** @return user first name */ public String getFirstName() { return firstName; }
    /** @param firstName user first name */ public void setFirstName(String firstName) { this.firstName = firstName; }
}
