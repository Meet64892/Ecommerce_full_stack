package com.smartshop.order.service;

import com.smartshop.order.entity.Order;
import com.smartshop.order.entity.OrderItem;
import com.smartshop.order.entity.OrderStatus;
import com.smartshop.order.event.InventoryCheckedEvent;
import com.smartshop.order.event.OrderCancelledEvent;
import com.smartshop.order.event.OrderConfirmedEvent;
import com.smartshop.order.event.OrderCreatedEvent;
import com.smartshop.order.kafka.OrderEventProducer;
import com.smartshop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SagaOrchestrator - Central coordinator of the "place order" saga.
 *
 * <h2>Purpose</h2>
 * Drives the multi-step, multi-service order flow: start (reserve inventory) ->
 * on success confirm the order; on failure compensate by cancelling it. Being a
 * single orchestrator (vs choreography) keeps the business flow readable and the
 * decision logic in one place.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Why not a distributed ACID transaction?</b> A single transaction
 *       spanning order DB + inventory DB would require two-phase commit (2PC).
 *       2PC holds locks across services for the whole flow, creating tight
 *       coupling and poor availability — if any participant or the coordinator
 *       stalls, everyone blocks. It does not scale for microservices.</li>
 *   <li><b>Eventual consistency</b>: instead, each service commits its OWN local
 *       transaction and we coordinate via events. The system is briefly
 *       inconsistent (order PENDING while inventory decides) but converges to a
 *       consistent end state (CONFIRMED or CANCELLED). Contrast with strong
 *       consistency where every read always sees the latest write immediately.</li>
 *   <li><b>Compensation</b>: there is no rollback across services; we undo with a
 *       compensating action (cancel the order) when a step fails.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * {@code startSaga} publishes ORDER_CREATED; {@code handleInventoryChecked} is
 * called by the Kafka consumer with inventory's reply and finishes the saga.
 *
 * @author SmartShop Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SagaOrchestrator {

    private final OrderRepository orderRepository;
    private final OrderEventProducer eventProducer;

    /**
     * Step 1 of the saga: ask inventory to reserve stock for the order.
     *
     * @param order the freshly created (PENDING) order
     */
    public void startSaga(Order order) {
        // Build productId -> quantity map for the reservation request.
        Map<Long, Integer> quantities = new LinkedHashMap<>();
        for (OrderItem item : order.getItems()) {
            // merge handles the case where the same product appears twice.
            quantities.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }

        OrderCreatedEvent event = new OrderCreatedEvent(order.getId(), order.getUserId(), quantities);
        eventProducer.publishOrderCreated(event);
        log.info("Saga started for order {} -> awaiting inventory check", order.getId());
    }

    /**
     * Final step: react to inventory's decision. Confirm on success, cancel
     * (compensate) on failure. Idempotent — re-processing a duplicate event for
     * an already-finalized order is a no-op.
     *
     * @param event inventory's reservation result
     */
    @Transactional
    public void handleInventoryChecked(InventoryCheckedEvent event) {
        Order order = orderRepository.findById(event.getOrderId()).orElse(null);
        if (order == null) {
            log.warn("Received inventory check for unknown order {}", event.getOrderId());
            return;
        }

        // Idempotency guard: if the order already left PENDING, we've handled it.
        if (order.getStatus() != OrderStatus.PENDING) {
            log.info("Order {} already in state {}, ignoring duplicate inventory event",
                    order.getId(), order.getStatus());
            return;
        }

        if (event.isReserved()) {
            // Success path: move to CONFIRMED and notify downstream.
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
            eventProducer.publishOrderConfirmed(new OrderConfirmedEvent(order.getId(), order.getUserId()));
            log.info("Order {} CONFIRMED", order.getId());
        } else {
            // Failure path: compensate by cancelling and notify of the reason.
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            eventProducer.publishOrderCancelled(
                    new OrderCancelledEvent(order.getId(), order.getUserId(), event.getReason()));
            log.info("Order {} CANCELLED ({})", order.getId(), event.getReason());
        }
    }
}
