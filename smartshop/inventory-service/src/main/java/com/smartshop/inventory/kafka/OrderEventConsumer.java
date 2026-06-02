package com.smartshop.inventory.kafka;

import com.smartshop.inventory.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * OrderEventConsumer - Consumes order-created events and reserves stock.
 *
 * <h2>Purpose</h2>
 * Inventory is updated asynchronously after checkout begins. Kafka is better than direct HTTP here because order-service
 * can publish once and inventory-service can process when available without blocking the user request thread.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Consumer group: Inventory instances in the same group divide work while preserving one processing result.</li>
 *   <li>Idempotency: Production code would persist processed event ids to avoid duplicate reservations.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * The consumer reserves every line item and publishes inventory.checked back to order-service.
 *
 * @see InventoryService
 * @author SmartShop Team
 */
@Slf4j
@Component
public class OrderEventConsumer {
    private final InventoryService inventoryService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Creates the consumer with stock service and Kafka producer dependencies.
     *
     * @param inventoryService stock reservation service
     * @param kafkaTemplate publisher for inventory.checked replies
     */
    public OrderEventConsumer(InventoryService inventoryService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.inventoryService = inventoryService;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Reserves stock for each item in an order-created event.
     *
     * @param event order-created message
     */
    @KafkaListener(topics = "order.created", groupId = "inventory-service")
    public void onOrderCreated(OrderCreatedEvent event) {
        try {
            for (OrderItemMessage item : event.getItems()) {
                inventoryService.reserve(item.getProductId(), item.getQuantity());
            }
            kafkaTemplate.send("inventory.checked", event.getOrderId().toString(), new InventoryCheckedEvent(event.getOrderId(), true, "Reserved"));
        } catch (RuntimeException ex) {
            log.warn("Inventory reservation failed for order {}", event.getOrderId(), ex);
            kafkaTemplate.send("inventory.checked", event.getOrderId().toString(), new InventoryCheckedEvent(event.getOrderId(), false, ex.getMessage()));
        }
    }
}
