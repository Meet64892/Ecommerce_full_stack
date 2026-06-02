package com.smartshop.inventory.kafka;

import com.smartshop.inventory.exception.InsufficientStockException;
import com.smartshop.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * OrderEventConsumer - Consumes order.created events and reserves stock.
 *
 * <h2>Purpose</h2>
 * Executes inventory reservation step of order saga and publishes result events.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Consumer group isolation: inventory has independent stream processing.</li>
 *   <li>Dead-letter topics: failed records should be redirected for forensic replay in production.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Bridges order created events to inventory checked responses.
 *
 * @see com.smartshop.inventory.service.InventoryService
 * @author SmartShop Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final InventoryService inventoryService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Processes order.created event by attempting stock reservation.
     *
     * @param event order created event
     */
    @KafkaListener(topics = "order.created", groupId = "inventory-service-group")
    public void onOrderCreated(final OrderCreatedEvent event) {
        try {
            // Demo assumption: reserve one unit of productId=1; real system sends full line items.
            inventoryService.reserveStock(1L, 1);
            kafkaTemplate.send("inventory.checked", String.valueOf(event.orderId()),
                    new InventoryCheckedEvent(event.orderId(), true, "Stock reserved"));
        } catch (InsufficientStockException | IllegalArgumentException ex) {
            kafkaTemplate.send("inventory.checked", String.valueOf(event.orderId()),
                    new InventoryCheckedEvent(event.orderId(), false, ex.getMessage()));
        } catch (Exception ex) {
            log.error("Unexpected reservation failure for order {}", event.orderId(), ex);
            kafkaTemplate.send("inventory.checked", String.valueOf(event.orderId()),
                    new InventoryCheckedEvent(event.orderId(), false, "Unexpected inventory error"));
        }
    }
}
