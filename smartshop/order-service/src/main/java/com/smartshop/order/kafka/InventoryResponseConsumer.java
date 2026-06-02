package com.smartshop.order.kafka;

import com.smartshop.order.event.InventoryCheckedEvent;
import com.smartshop.order.service.SagaOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * InventoryResponseConsumer - Listens for inventory's reservation replies.
 *
 * <h2>Purpose</h2>
 * Bridges Kafka into the saga: when inventory-service publishes an
 * {@link InventoryCheckedEvent}, this consumer hands it to the orchestrator to
 * finalize the order.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>@KafkaListener</b>: subscribes this method to a topic within a consumer
 *       group; Spring deserializes the JSON payload into the event type.</li>
 *   <li><b>Idempotency</b>: because Kafka is at-least-once, the same event may
 *       arrive twice. The orchestrator's status guard makes re-processing safe.</li>
 *   <li><b>Offset</b>: Kafka tracks how far this group has consumed; on success
 *       the offset advances so we don't reprocess on restart.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Consumes {@code inventory.checked}; delegates to {@link SagaOrchestrator}.
 *
 * @author SmartShop Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryResponseConsumer {

    private final SagaOrchestrator sagaOrchestrator;

    /**
     * Handles an inventory reservation result.
     *
     * @param event the inventory check reply (auto-deserialized from JSON)
     */
    @KafkaListener(topics = "inventory.checked", groupId = "order-service")
    public void onInventoryChecked(InventoryCheckedEvent event) {
        log.info("Consumed INVENTORY_CHECKED for order {} reserved={} (eventId={})",
                event.getOrderId(), event.isReserved(), event.getEventId());
        sagaOrchestrator.handleInventoryChecked(event);
    }
}
