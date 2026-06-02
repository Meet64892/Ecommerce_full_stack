package com.smartshop.order.kafka;

import com.smartshop.common.event.order.InventoryCheckedEvent;
import com.smartshop.order.service.SagaOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * InventoryResponseConsumer - Kafka Consumer for inventory-service Replies
 *
 * <h2>Purpose</h2>
 * Listens for InventoryCheckedEvent replies from inventory-service and
 * forwards them to the SagaOrchestrator to advance or compensate the Saga.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>@KafkaListener: Marks a method as a Kafka message consumer.
 *       The container factory configured in KafkaConfig handles:
 *       - Deserialization (JSON → InventoryCheckedEvent)
 *       - Error handling (retry, dead letter)
 *       - Offset management (auto-commit or manual)</li>
 *   <li>Consumer Groups: All instances of order-service share the same consumer group
 *       ("order-service-group"). Kafka ensures each message is delivered to exactly
 *       ONE instance in the group — this prevents double-processing when scaled.</li>
 *   <li>Offset Management: Kafka tracks which messages each consumer group has processed
 *       via "offsets". After processing a message, the consumer "commits" the offset
 *       to tell Kafka "I've processed up to message #N".
 *       Auto-commit: Kafka commits automatically every 5 seconds (simpler but risky —
 *       if the service crashes after commit but before processing, the message is lost).
 *       Manual commit: Commit only after successful processing (at-least-once).</li>
 *   <li>Idempotency: Since Kafka delivers at-least-once, the same InventoryCheckedEvent
 *       might arrive twice. SagaOrchestrator must be idempotent — checking if the order
 *       is already in the expected state before processing.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryResponseConsumer {

    private final SagaOrchestrator sagaOrchestrator;

    /**
     * Processes inventory check results.
     * Delegates to SagaOrchestrator which decides the next Saga step:
     *   - stockAvailable=true → confirm the order
     *   - stockAvailable=false → cancel the order (compensating transaction)
     *
     * @param event      the deserialized InventoryCheckedEvent
     * @param partition  the Kafka partition this message came from (for logging)
     * @param offset     the Kafka offset of this message (for logging)
     */
    @KafkaListener(
        topics = "inventory.checked",
        groupId = "${spring.kafka.consumer.group-id:order-service-group}",
        // containerFactory specifies which ConcurrentKafkaListenerContainerFactory to use
        // We use "kafkaListenerContainerFactory" defined in KafkaConfig
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleInventoryChecked(
            @Payload InventoryCheckedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received InventoryCheckedEvent: orderId={} stockAvailable={} partition={} offset={}",
                event.getOrderId(), event.isStockAvailable(), partition, offset);

        try {
            // Delegate to Saga orchestrator
            sagaOrchestrator.handleInventoryChecked(event);
        } catch (Exception e) {
            // Log error but don't re-throw — Spring Kafka will retry based on config
            // After max retries, the message goes to the Dead Letter Topic
            log.error("Failed to process InventoryCheckedEvent for orderId={}: {}",
                    event.getOrderId(), e.getMessage(), e);
            throw e; // Re-throw to trigger Spring Kafka's retry mechanism
        }
    }
}
