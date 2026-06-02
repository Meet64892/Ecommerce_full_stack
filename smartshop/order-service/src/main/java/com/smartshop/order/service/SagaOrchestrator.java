package com.smartshop.order.service;

import com.smartshop.order.entity.Order;
import com.smartshop.order.entity.OrderStatus;
import com.smartshop.order.event.InventoryCheckedEvent;
import com.smartshop.order.event.OrderCancelledEvent;
import com.smartshop.order.event.OrderConfirmedEvent;
import com.smartshop.order.event.OrderCreatedEvent;
import com.smartshop.order.kafka.OrderEventProducer;
import com.smartshop.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * SagaOrchestrator - Coordinates distributed order workflow.
 *
 * <h2>Purpose</h2>
 * Saga pattern replaces distributed ACID transactions by coordinating local transactions through
 * events. Orchestration centralizes flow control versus choreography where each service reacts
 * independently.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Steps: inventory check -> payment (simulated) -> confirmation/cancellation.</li>
 *   <li>Compensation: failed step triggers cancellation event.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Starts workflow on new order and handles asynchronous inventory responses.
 *
 * @see com.smartshop.order.event.InventoryCheckedEvent
 * @author SmartShop Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SagaOrchestrator {

    private final OrderEventProducer eventProducer;
    private final OrderRepository orderRepository;

    /**
     * Starts saga by requesting inventory reservation.
     *
     * @param order created order aggregate
     */
    public void startSaga(final Order order) {
        eventProducer.publishOrderCreated(new OrderCreatedEvent(order.getId(), order.getUserId(), order.getTotalAmount()));
        log.info("Saga started for order {}", order.getId());
    }

    /**
     * Applies inventory response and transitions order state.
     *
     * @param event inventory response
     */
    @Transactional
    public void handleInventoryChecked(final InventoryCheckedEvent event) {
        final Order order = orderRepository.findById(event.getOrderId()).orElse(null);
        if (order == null) {
            log.warn("Order {} not found while handling inventory response", event.getOrderId());
            return;
        }

        // Idempotency: if order already left PENDING, duplicate Kafka deliveries are ignored.
        if (order.getStatus() != OrderStatus.PENDING) {
            log.info("Ignoring duplicate inventory event for order {} with status {}", order.getId(), order.getStatus());
            return;
        }

        if (event.isAvailable()) {
            // Payment step is simulated as successful in this educational project.
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
            eventProducer.publishOrderConfirmed(new OrderConfirmedEvent(order.getId()));
        } else {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            eventProducer.publishOrderCancelled(new OrderCancelledEvent(order.getId(), event.getReason()));
        }
    }
}
