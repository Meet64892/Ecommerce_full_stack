package com.smartshop.order.kafka;

import com.smartshop.order.event.InventoryCheckedEvent;
import com.smartshop.order.service.SagaOrchestrator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * InventoryResponseConsumer - Consumes inventory reservation replies.
 *
 * <h2>Purpose</h2>
 * Kafka consumers advance asynchronous workflows by reacting to events. Because Kafka can redeliver messages after
 * retries or crashes, consumer logic must be idempotent and safe to run more than once.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Consumer group: `order-service` group receives one copy of each inventory reply across its instances.</li>
 *   <li>Offset: Kafka remembers which records this group has processed.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * This consumer delegates to SagaOrchestrator, which updates order status and publishes follow-up events.
 *
 * @see SagaOrchestrator
 * @author SmartShop Team
 */
@Slf4j
@Component
public class InventoryResponseConsumer {
    private final SagaOrchestrator sagaOrchestrator;

    /**
     * Creates the consumer with its Saga coordinator.
     *
     * @param sagaOrchestrator Saga coordinator
     */
    public InventoryResponseConsumer(SagaOrchestrator sagaOrchestrator) {
        this.sagaOrchestrator = sagaOrchestrator;
    }

    /**
     * Handles messages from the inventory.checked topic.
     *
     * @param event inventory reservation result
     */
    @KafkaListener(topics = "inventory.checked", groupId = "order-service")
    public void handleInventoryChecked(InventoryCheckedEvent event) {
        log.info("Received inventory result for order {} reserved={}", event.getOrderId(), event.isReserved());
        sagaOrchestrator.handleInventoryResponse(event);
    }
}
