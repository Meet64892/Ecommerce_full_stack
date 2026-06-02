package com.smartshop.order.service;

import com.smartshop.order.entity.Order;
import com.smartshop.order.entity.OrderStatus;
import com.smartshop.common.event.order.InventoryCheckedEvent;
import com.smartshop.common.event.order.OrderCancelledEvent;
import com.smartshop.common.event.order.OrderConfirmedEvent;
import com.smartshop.order.kafka.OrderEventProducer;
import com.smartshop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SagaOrchestrator - Orchestrates the Order Placement Saga
 *
 * <h2>Purpose</h2>
 * Manages the distributed transaction for placing an order.
 * The Saga pattern breaks a distributed transaction into a sequence of local
 * transactions, each with a corresponding compensating transaction for rollback.
 *
 * <h2>The Order Placement Saga Flow</h2>
 * <pre>
 * Step 1: order-service creates Order (PENDING) + publishes OrderCreatedEvent
 *              ↓
 * Step 2: inventory-service reserves stock + publishes InventoryCheckedEvent
 *              ↓
 * Step 3 (Success): SagaOrchestrator confirms Order (CONFIRMED) + publishes OrderConfirmedEvent
 * Step 3 (Failure): SagaOrchestrator cancels Order (CANCELLED) + publishes OrderCancelledEvent
 *              ↓
 * Step 4 (on cancel): inventory-service releases reserved stock (compensating transaction)
 * </pre>
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Why Saga instead of 2-Phase Commit (2PC)?
 *       2PC requires all participants to lock resources and vote. Problems:
 *       - Blocking: all services are locked while coordinator decides
 *       - Single point of failure: coordinator failure = all services stuck
 *       - Scale: doesn't work across microservices with different DB technologies
 *       Saga: no distributed lock, services process sequentially, failures trigger
 *       compensating transactions. Trade-off: eventual consistency vs strong consistency.</li>
 *   <li>Compensating Transactions: "Undo" operations for each Saga step.
 *       Step 1 compensation: cancel the order
 *       Step 2 compensation: release inventory reservation
 *       Compensating transactions must be idempotent (safe to call multiple times).</li>
 *   <li>Idempotency: This orchestrator checks order.getStatus() before processing.
 *       If the order is already CONFIRMED, a duplicate InventoryCheckedEvent is ignored.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SagaOrchestrator {

    private final OrderRepository orderRepository;
    private final OrderEventProducer eventProducer;

    /**
     * Handles the result of the inventory check step in the Saga.
     * Determines whether to advance to CONFIRMED or compensate with CANCELLED.
     * This method is @Transactional to ensure the order status update and
     * any DB changes are atomic — either both happen or neither does.
     *
     * @param event the inventory check result from inventory-service
     */
    @Transactional
    public void handleInventoryChecked(InventoryCheckedEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> {
                    log.error("Order {} not found when processing InventoryCheckedEvent", event.getOrderId());
                    return new IllegalStateException("Order not found: " + event.getOrderId());
                });

        // Idempotency check: if order is not in PENDING state, this is a duplicate event
        // (at-least-once delivery means we might receive this event more than once)
        if (order.getStatus() != OrderStatus.PENDING) {
            log.warn("Ignoring duplicate InventoryCheckedEvent for order={} (current status={})",
                    event.getOrderId(), order.getStatus());
            return;
        }

        if (event.isStockAvailable()) {
            // SAGA SUCCESS PATH: inventory reserved, proceed to confirm
            confirmOrder(order);
        } else {
            // SAGA FAILURE PATH: inventory unavailable, compensate by cancelling
            cancelOrder(order, event.getFailureReason());
        }
    }

    /**
     * Advances the order to CONFIRMED status and publishes the confirmation event.
     * notification-service will send a confirmation email upon receiving OrderConfirmedEvent.
     *
     * @param order the order to confirm
     */
    private void confirmOrder(Order order) {
        log.info("Confirming order id={} correlationId={}", order.getId(), order.getCorrelationId());

        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        // Publish confirmation event — notification-service will email the customer
        OrderConfirmedEvent confirmedEvent = OrderConfirmedEvent.of(
                order.getId(),
                order.getUserId(),
                null,  // userEmail would be fetched from user-service in a full implementation
                order.getCorrelationId()
        );
        eventProducer.publishOrderConfirmed(confirmedEvent);

        log.info("Order id={} confirmed successfully", order.getId());
    }

    /**
     * Cancels the order (compensating transaction) and publishes a cancellation event.
     * inventory-service will release any partially reserved stock upon receiving this event.
     *
     * @param order  the order to cancel
     * @param reason the reason for cancellation (displayed to customer)
     */
    private void cancelOrder(Order order, String reason) {
        log.warn("Cancelling order id={} due to: {}", order.getId(), reason);

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Publish cancellation event — inventory-service releases stock
        OrderCancelledEvent cancelledEvent = OrderCancelledEvent.of(
                order.getId(),
                order.getUserId(),
                reason,
                order.getCorrelationId()
        );
        eventProducer.publishOrderCancelled(cancelledEvent);

        log.info("Order id={} cancelled", order.getId());
    }
}
