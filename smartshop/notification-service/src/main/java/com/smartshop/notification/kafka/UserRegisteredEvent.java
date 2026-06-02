package com.smartshop.notification.kafka;

import java.util.UUID;

/**
 * UserRegisteredEvent - Local notification view of a user registration event.
 *
 * <h2>Purpose</h2>
 * Welcome emails are triggered asynchronously so registration stays fast and resilient. The event carries enough user
 * data to render a greeting without querying user-service.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Data duplication in events: Events include facts consumers need at processing time.</li>
 *   <li>Consumer autonomy: Notification-service can process this event even if user-service is down.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * UserEventConsumer parses this DTO from the `user.registered` topic.
 *
 * @see UserEventConsumer
 * @author SmartShop Team
 */
public class UserRegisteredEvent {
    private UUID userId;
    private String email;
    private String firstName;
    /** Required by Jackson. */ public UserRegisteredEvent() {}
    /** @return user id */ public UUID getUserId() { return userId; }
    /** @param userId user id */ public void setUserId(UUID userId) { this.userId = userId; }
    /** @return email */ public String getEmail() { return email; }
    /** @param email email */ public void setEmail(String email) { this.email = email; }
    /** @return first name */ public String getFirstName() { return firstName; }
    /** @param firstName first name */ public void setFirstName(String firstName) { this.firstName = firstName; }
}
