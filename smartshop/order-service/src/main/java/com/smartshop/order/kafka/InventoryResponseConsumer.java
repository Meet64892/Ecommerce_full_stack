package com.smartshop.order.kafka;

import com.smartshop.order.event.InventoryCheckedEvent;
import com.smartshop.order.service.SagaOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * InventoryResponseConsumer - Consumes inventory reservation responses.
 *
 * <h2>Purpose</h2>
 * Receives asynchronous inventory outcomes and forwards them to saga coordinator for state
 * transition decisions.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Consumer groups: each group processes full topic stream independently.</li>
 *   <li>Idempotent handling: duplicate messages should not corrupt order state.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Listens on inventory.checked topic and invokes saga transition logic.
 *
 * @see com.smartshop.order.service.SagaOrchestrator
 * @author SmartShop Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryResponseConsumer {

    private final SagaOrchestrator sagaOrchestrator;

    /**
     * Processes inventory checked event.
     *
     * @param event inventory response event
     */
    @KafkaListener(topics = "inventory.checked", groupId = "order-service-group")
    public void onInventoryChecked(final InventoryCheckedEvent event) {
        // Idempotency guard is implemented inside saga orchestrator by checking current order status.
        sagaOrchestrator.handleInventoryChecked(event);
        log.info("Processed inventory response for order {}", event.getOrderId());
    }
}
