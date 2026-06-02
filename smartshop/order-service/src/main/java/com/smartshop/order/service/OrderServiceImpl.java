package com.smartshop.order.service;

import com.smartshop.order.dto.CreateOrderRequest;
import com.smartshop.order.dto.OrderDto;
import com.smartshop.order.dto.OrderItemDto;
import com.smartshop.order.entity.Order;
import com.smartshop.order.entity.OrderItem;
import com.smartshop.order.entity.OrderStatus;
import com.smartshop.order.exception.OrderNotFoundException;
import com.smartshop.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * OrderServiceImpl - Order creation and query implementation.
 *
 * <h2>Purpose</h2>
 * Persists initial order state then delegates distributed transaction orchestration to saga
 * coordinator. This avoids using 2PC, which does not scale well for autonomous microservices.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Saga over 2PC: local transactions with compensating actions.</li>
 *   <li>@Transactional: create flow is atomic within order-service database.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Called by controller endpoints and triggers saga orchestrator for async progression.
 *
 * @see SagaOrchestrator
 * @author SmartShop Team
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final SagaOrchestrator sagaOrchestrator;

    /**
     * Creates order in PENDING state and emits order.created event.
     *
     * @param request create request
     * @return created order dto
     */
    @Override
    @Transactional
    public OrderDto createOrder(final CreateOrderRequest request) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemDto item : request.items()) {
            total = total.add(item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())));
        }

        final Order order = Order.builder()
                .userId(request.userId())
                .status(OrderStatus.PENDING)
                .totalAmount(total)
                .build();

        final List<OrderItem> items = request.items().stream()
                .map(item -> OrderItem.builder()
                        .order(order)
                        .productId(item.productId())
                        .quantity(item.quantity())
                        .unitPrice(item.unitPrice())
                        .build())
                .toList();
        order.setItems(items);

        final Order saved = orderRepository.save(order);
        sagaOrchestrator.startSaga(saved);
        return toDto(saved);
    }

    /**
     * Returns order by id.
     *
     * @param id order id
     * @return order dto
     */
    @Override
    public OrderDto getById(final Long id) {
        return orderRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + id));
    }

    /**
     * Returns user orders.
     *
     * @param userId user id
     * @return order list
     */
    @Override
    public List<OrderDto> getByUserId(final Long userId) {
        return orderRepository.findByUserId(userId).stream().map(this::toDto).toList();
    }

    /**
     * Maps order aggregate to API DTO.
     *
     * @param order aggregate
     * @return dto
     */
    private OrderDto toDto(final Order order) {
        final List<OrderItemDto> items = order.getItems().stream()
                .map(item -> new OrderItemDto(item.getProductId(), item.getQuantity(), item.getUnitPrice()))
                .toList();
        return new OrderDto(order.getId(), order.getUserId(), order.getStatus(), order.getTotalAmount(), items, order.getCreatedAt());
    }
}
