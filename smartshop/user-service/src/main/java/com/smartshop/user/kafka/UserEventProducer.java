package com.smartshop.user.kafka;

import com.smartshop.user.event.UserRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * UserEventProducer - Publishes user-domain events to Kafka.
 *
 * <h2>Purpose</h2>
 * User-service should not directly call notification-service for welcome emails. Publishing a Kafka event keeps
 * registration fast and lets notification-service retry or fail independently.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Topic: `user.registered` stores account-created facts.</li>
 *   <li>At-least-once delivery: Consumers must tolerate duplicate welcome-event processing.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * UserServiceImpl calls this producer after saving a new user.
 *
 * @see UserRegisteredEvent
 * @author SmartShop Team
 */
@Slf4j
@Component
public class UserEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Creates the producer with KafkaTemplate.
     *
     * @param kafkaTemplate template for publishing JSON events
     */
    public UserEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a user-registered event.
     *
     * @param event registration event
     */
    public void publishRegistered(UserRegisteredEvent event) {
        kafkaTemplate.send("user.registered", event.getUserId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.warn("Failed to publish user.registered for {}", event.getUserId(), ex);
                    } else {
                        log.info("Published user.registered for {}", event.getUserId());
                    }
                });
    }
}
