package com.smartshop.notification.event;

import com.smartshop.common.event.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * UserRegisteredEvent - Event signaling a new account was created.
 *
 * <h2>Purpose</h2>
 * Triggers a welcome email. Defined here for the consumer; a producing service
 * (user-service) would emit the same structure when wired up.
 *
 * <h2>How it fits in the system</h2>
 * Consumed by {@code UserEventConsumer} to send a welcome email.
 *
 * @author SmartShop Team
 */
@Getter
@Setter
@NoArgsConstructor
public class UserRegisteredEvent extends BaseEvent {

    /** The new user's id. */
    private Long userId;

    /** The new user's email (recipient). */
    private String email;

    /** The new user's display name (greeting). */
    private String fullName;
}
