package com.smartshop.order.service;

import com.smartshop.common.exception.ResourceNotFoundException;
import com.smartshop.order.dto.OrderItemDto;
import com.smartshop.order.entity.Order;
import com.smartshop.order.entity.OrderStatus;
import com.smartshop.order.event.InventoryCheckedEvent;
import com.smartshop.order.event.OrderCancelledEvent;
import com.smartshop.order.event.OrderConfirmedEvent;
import com.smartshop.order.event.OrderCreatedEvent;
import com.smartshop.order.kafka.OrderEventProducer;
import com.smartshop.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SagaOrchestrator - Coordinates the order checkout Saga.
 *
 * <h2>Purpose</h2>
 * Distributed transactions and two-phase commit do not scale well across autonomous microservices because they lock
 * resources and couple service availability. A Saga replaces one global transaction with local transactions and events.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Orchestration: One coordinator tells participants what step happens next.</li>
 *   <li>Choreography: An alternative where services react to events without a central coordinator.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * OrderServiceImpl starts the Saga, inventory-service replies, and this orchestrator confirms or cancels the order.
 *
 * @see OrderEventProducer
 * @author SmartShop Team
 */
@Service
public class SagaOrchestrator {
    private final OrderRepository orderRepository;
    private final OrderEventProducer eventProducer;

    /**
     * Creates the orchestrator with persistence and event dependencies.
     *
     * @param orderRepository order persistence gateway
     * @param eventProducer Kafka publisher
     */
    public SagaOrchestrator(OrderRepository orderRepository, OrderEventProducer eventProducer) {
        this.orderRepository = orderRepository;
        this.eventProducer = eventProducer;
    }

    /**
     * Starts the inventory reservation step for a pending order.
     *
     * @param order saved order aggregate
     */
    public void start(Order order) {
        List<OrderItemDto> items = order.getItems().stream()
                .map(item -> new OrderItemDto(
                        item.getId(),
                        item.getProductId(),
                        item.getBrandId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice()))
                .toList();
        eventProducer.publish(OrderEventProducer.ORDER_CREATED, order.getId().toString(), new OrderCreatedEvent(order.getId(), order.getUserId(), items));
    }

    /**
     * Handles an inventory reply idempotently by ignoring already-finalized orders.
     *
     * @param event inventory reservation result
     * @throws ResourceNotFoundException when the order id in the reply does not exist
     */
    @Transactional
    public void handleInventoryResponse(InventoryCheckedEvent event) {
        Order order = orderRepository.findById(event.getOrderId()).orElseThrow(() -> new ResourceNotFoundException("Order", event.getOrderId()));
        if (order.getStatus() != OrderStatus.PENDING) {
            return;
        }
        if (event.isReserved()) {
            order.setStatus(OrderStatus.CONFIRMED);
            eventProducer.publish(OrderEventProducer.ORDER_CONFIRMED, order.getId().toString(), new OrderConfirmedEvent(order.getId(), order.getUserId()));
        } else {
            order.setStatus(OrderStatus.CANCELLED);
            eventProducer.publish(OrderEventProducer.ORDER_CANCELLED, order.getId().toString(), new OrderCancelledEvent(order.getId(), order.getUserId(), event.getReason()));
        }
    }
}
