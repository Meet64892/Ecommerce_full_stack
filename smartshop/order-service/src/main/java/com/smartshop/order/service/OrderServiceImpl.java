package com.smartshop.order.service;

import com.smartshop.common.exception.ResourceNotFoundException;
import com.smartshop.order.dto.CreateOrderRequest;
import com.smartshop.order.dto.OrderDto;
import com.smartshop.order.dto.OrderItemDto;
import com.smartshop.order.entity.Order;
import com.smartshop.order.entity.OrderItem;
import com.smartshop.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * OrderServiceImpl - Implements checkout and order history use cases.
 *
 * <h2>Purpose</h2>
 * The service creates a durable pending order before publishing Kafka events. This local transaction-first approach
 * makes the order recoverable even if downstream services are temporarily unavailable.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>ACID: The database commit atomically stores order and item rows.</li>
 *   <li>Eventual consistency: Confirmation happens later after inventory responds.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * OrderController calls this class, which saves the aggregate and asks SagaOrchestrator to continue the workflow.
 *
 * @see SagaOrchestrator
 * @author SmartShop Team
 */
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final SagaOrchestrator sagaOrchestrator;

    /**
     * Creates the service with repository and Saga dependencies.
     *
     * @param orderRepository order persistence gateway
     * @param sagaOrchestrator checkout Saga coordinator
     */
    public OrderServiceImpl(OrderRepository orderRepository, SagaOrchestrator sagaOrchestrator) {
        this.orderRepository = orderRepository;
        this.sagaOrchestrator = sagaOrchestrator;
    }

    /**
     * Creates a pending order and publishes the inventory reservation request.
     *
     * @param request validated checkout command
     * @return created order DTO
     */
    @Override
    @Transactional
    public OrderDto createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setUserId(request.userId());
        request.items().forEach(item -> order.addItem(new OrderItem(item.productId(), item.quantity(), item.unitPrice())));
        Order saved = orderRepository.save(order);
        sagaOrchestrator.start(saved);
        return toDto(saved);
    }

    /**
     * Loads an order by id.
     *
     * @param id order id
     * @return order DTO
     * @throws ResourceNotFoundException when no order exists
     */
    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrder(UUID id) {
        return orderRepository.findById(id).map(this::toDto).orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    /**
     * Lists all orders for a user.
     *
     * @param userId buyer id
     * @return order DTO list
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> ordersForUser(UUID userId) {
        return orderRepository.findByUserId(userId).stream().map(this::toDto).toList();
    }

    /**
     * Maps an Order aggregate to an API DTO.
     *
     * @param order persistent order aggregate
     * @return immutable order DTO
     */
    private OrderDto toDto(Order order) {
        List<OrderItemDto> items = order.getItems().stream()
                .map(item -> new OrderItemDto(item.getId(), item.getProductId(), item.getQuantity(), item.getUnitPrice()))
                .toList();
        return new OrderDto(order.getId(), order.getUserId(), order.getStatus(), order.getTotalAmount(), items, order.getCreatedAt());
    }
}
