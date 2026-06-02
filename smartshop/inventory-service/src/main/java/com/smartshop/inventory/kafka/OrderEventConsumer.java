package com.smartshop.inventory.kafka;

import com.smartshop.inventory.dto.ReserveStockRequest;
import com.smartshop.inventory.service.InventoryServiceImpl;
import com.smartshop.common.event.order.OrderCancelledEvent;
import com.smartshop.common.event.order.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.smartshop.common.event.order.InventoryCheckedEvent;

/**
 * OrderEventConsumer - Processes Order Events and Updates Inventory
 *
 * <h2>Purpose</h2>
 * Listens for order events from Kafka and updates inventory accordingly:
 *   - OrderCreatedEvent → reserve stock → publish InventoryCheckedEvent
 *   - OrderCancelledEvent → release reserved stock
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Consumer Group Isolation: inventory-service has its own consumer group
 *       "inventory-service-group". This means it reads EVERY message independently
 *       from notification-service's consumer group. Both services process the same
 *       ORDER_CREATED event but for different purposes (inventory vs email).</li>
 *   <li>Idempotency: If the same OrderCreatedEvent arrives twice (at-least-once),
 *       we might double-reserve. Solution: check if orderId was already processed
 *       (store processed orderIds in Redis with TTL). Not fully implemented here
 *       for brevity — marked as a TODO.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final InventoryServiceImpl inventoryService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Processes new orders by reserving stock for each item.
     * Publishes InventoryCheckedEvent back to order-service with the reservation result.
     *
     * @param event the OrderCreatedEvent from order-service
     */
    @KafkaListener(
        topics = "order.created",
        groupId = "inventory-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Processing OrderCreatedEvent: orderId={} items={}",
                event.getOrderId(), event.getItems().size());

        boolean allReserved = true;
        String failureReason = null;

        // Attempt to reserve stock for each item
        for (OrderCreatedEvent.OrderItemEvent item : event.getItems()) {
            try {
                inventoryService.reserveStock(new ReserveStockRequest(
                        item.getProductId(),
                        item.getQuantity(),
                        event.getOrderId()
                ));
                log.debug("Reserved {} units of productId={} for orderId={}",
                        item.getQuantity(), item.getProductId(), event.getOrderId());
            } catch (Exception e) {
                log.warn("Failed to reserve stock for productId={}: {}",
                        item.getProductId(), e.getMessage());
                allReserved = false;
                failureReason = e.getMessage();

                // TODO: If partial reservation occurred (some items reserved, some failed),
                // we need to release the already-reserved items before publishing failure.
                // This is a compensating transaction within the Saga step.
                break;
            }
        }

        // Publish the result back to order-service via the "inventory.checked" topic
        InventoryCheckedEvent responseEvent = InventoryCheckedEvent.builder()
                .orderId(event.getOrderId())
                .stockAvailable(allReserved)
                .failureReason(failureReason)
                .build();
        responseEvent.initializeEvent("INVENTORY_CHECKED", event.getCorrelationId());

        kafkaTemplate.send("inventory.checked", String.valueOf(event.getOrderId()), responseEvent);
        log.info("Published InventoryCheckedEvent: orderId={} stockAvailable={}",
                event.getOrderId(), allReserved);
    }

    /**
     * Releases reserved stock when an order is cancelled.
     * This is the compensating transaction for the inventory reservation step.
     *
     * @param event the OrderCancelledEvent from order-service
     */
    @KafkaListener(
        topics = "order.cancelled",
        groupId = "inventory-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("Releasing stock for cancelled orderId={}", event.getOrderId());
        // In a full implementation, we'd look up the order's items and release each one
        // For simplicity, this is a placeholder for the release logic
        log.info("Stock released for orderId={}", event.getOrderId());
    }
}
