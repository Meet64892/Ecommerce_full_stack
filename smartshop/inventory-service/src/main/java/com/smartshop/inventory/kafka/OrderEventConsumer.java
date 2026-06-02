package com.smartshop.inventory.kafka;

import com.smartshop.inventory.config.KafkaConfig;
import com.smartshop.inventory.event.InventoryCheckedEvent;
import com.smartshop.inventory.event.OrderCreatedEvent;
import com.smartshop.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * OrderEventConsumer - Reserves stock when an order is created, then replies.
 *
 * <h2>Purpose</h2>
 * The inventory side of the saga step: consume {@code order.created}, try to
 * reserve every requested product, and publish an {@link InventoryCheckedEvent}
 * telling the order service the outcome.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Idempotency</b>: Kafka delivers at-least-once, so the same
 *       order.created may arrive twice. Reservation is naturally guarded by the
 *       saga's status check on the order side, and a production system would also
 *       track processed eventIds here. We log the eventId to make that explicit.</li>
 *   <li><b>All-or-nothing reply</b>: if ANY product can't be reserved we report
 *       failure so the saga compensates (cancels) the whole order.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Consumes {@code order.created}; produces {@code inventory.checked}.
 *
 * @author SmartShop Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final InventoryService inventoryService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Reserves stock for the order and replies with the result.
     *
     * @param event the order-created event (deserialized from JSON)
     */
    @KafkaListener(topics = "order.created", groupId = "inventory-service")
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Consumed ORDER_CREATED for order {} (eventId={})", event.getOrderId(), event.getEventId());

        boolean allReserved = true;
        String reason = null;

        // Attempt to reserve each line. First shortfall aborts the whole order.
        for (Map.Entry<Long, Integer> entry : event.getProductQuantities().entrySet()) {
            boolean ok = inventoryService.reserve(entry.getKey(), entry.getValue());
            if (!ok) {
                allReserved = false;
                reason = "Insufficient stock for product " + entry.getKey();
                break;
            }
        }

        InventoryCheckedEvent reply = new InventoryCheckedEvent(event.getOrderId(), allReserved, reason);
        // Key by orderId so the reply lands in the same partition ordering.
        kafkaTemplate.send(KafkaConfig.INVENTORY_CHECKED, String.valueOf(event.getOrderId()), reply);
        log.info("Replied INVENTORY_CHECKED for order {} reserved={}", event.getOrderId(), allReserved);
    }
}
