package com.smartshop.order.service;

import com.smartshop.common.exception.ResourceNotFoundException;
import com.smartshop.order.dto.CreateOrderRequest;
import com.smartshop.order.dto.OrderDto;
import com.smartshop.order.dto.OrderItemDto;
import com.smartshop.order.entity.Order;
import com.smartshop.order.entity.OrderItem;
import com.smartshop.order.entity.OrderStatus;
import com.smartshop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * OrderServiceImpl - Implements order placement and reads; entry to the saga.
 *
 * <h2>Purpose</h2>
 * Creates the PENDING order in a local transaction, then asks the
 * {@link SagaOrchestrator} to begin the distributed flow. Reads expose orders to
 * the API.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Local transaction first</b>: we COMMIT the order (PENDING) before
 *       publishing the start event, so the order durably exists when inventory
 *       replies. This ordering is central to the saga's correctness.</li>
 *   <li><b>Transaction propagation</b>: {@code @Transactional} defaults to
 *       REQUIRED — it joins an existing transaction or starts a new one. Other
 *       levels (REQUIRES_NEW, MANDATORY, NESTED, SUPPORTS) control whether a new
 *       physical transaction is created or an existing one is reused/required.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Called by {@code OrderController}; collaborates with the saga orchestrator.
 *
 * @author SmartShop Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final SagaOrchestrator sagaOrchestrator;

    /** {@inheritDoc} */
    @Override
    @Transactional
    public OrderDto placeOrder(CreateOrderRequest request) {
        Order order = Order.builder()
                .userId(request.userId())
                .status(OrderStatus.PENDING)   // every order starts PENDING.
                .totalAmount(BigDecimal.ZERO)  // recomputed below from items.
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemDto itemDto : request.items()) {
            OrderItem item = OrderItem.builder()
                    .productId(itemDto.productId())
                    .quantity(itemDto.quantity())
                    .unitPrice(itemDto.unitPrice())
                    .build();
            order.addItem(item);
            // Accumulate line total = unitPrice * quantity.
            total = total.add(itemDto.unitPrice().multiply(BigDecimal.valueOf(itemDto.quantity())));
        }
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        log.info("Placed order {} for user {} total={}", saved.getId(), saved.getUserId(), total);

        // Kick off the distributed saga (publishes ORDER_CREATED to Kafka).
        sagaOrchestrator.startSaga(saved);

        return toDto(saved);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public OrderDto getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        return toDto(order);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getByUser(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Maps the order aggregate to its DTO. Done by hand (not MapStruct) because
     * of the nested item list and to show the explicit conversion.
     *
     * @param order the entity
     * @return the DTO view
     */
    private OrderDto toDto(Order order) {
        List<OrderItemDto> items = order.getItems().stream()
                .map(i -> new OrderItemDto(i.getProductId(), i.getQuantity(), i.getUnitPrice()))
                .toList();
        return new OrderDto(order.getId(), order.getUserId(), order.getStatus(),
                order.getTotalAmount(), items, order.getCreatedAt());
    }
}
