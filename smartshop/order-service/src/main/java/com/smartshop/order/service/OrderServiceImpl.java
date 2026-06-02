package com.smartshop.order.service;

import com.smartshop.common.exception.ResourceNotFoundException;
import com.smartshop.order.dto.CreateOrderRequest;
import com.smartshop.order.dto.OrderDto;
import com.smartshop.order.dto.OrderItemDto;
import com.smartshop.order.entity.Order;
import com.smartshop.order.entity.OrderItem;
import com.smartshop.order.entity.OrderStatus;
import com.smartshop.common.event.order.OrderCreatedEvent;
import com.smartshop.order.kafka.OrderEventProducer;
import com.smartshop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * OrderServiceImpl - Order Placement and Management Business Logic
 *
 * @author SmartShop Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl {

    private final OrderRepository orderRepository;
    private final OrderEventProducer eventProducer;

    /**
     * Creates a new order and starts the Saga by publishing OrderCreatedEvent.
     *
     * @param userId  the authenticated customer placing the order
     * @param request the order details (items, shipping address)
     * @return the created order in PENDING status
     */
    public OrderDto createOrder(Long userId, CreateOrderRequest request) {
        log.info("Creating order for userId={} with {} items", userId, request.items().size());

        // Generate a unique correlation ID for this order's Saga
        // UUID ensures uniqueness across all service instances without coordination
        String correlationId = UUID.randomUUID().toString();

        // Build the Order entity
        Order order = Order.builder()
                .userId(userId)
                .correlationId(correlationId)
                .status(OrderStatus.PENDING)
                .shippingAddress(request.shippingAddress())
                .notes(request.notes())
                .totalAmount(BigDecimal.ZERO)  // Will be recalculated when items are added
                .build();

        // Add order items
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.items()) {
            OrderItem item = OrderItem.builder()
                    .productId(itemRequest.productId())
                    .productName(itemRequest.productName())
                    .sku(itemRequest.sku())
                    .quantity(itemRequest.quantity())
                    .unitPrice(itemRequest.unitPrice())
                    .build();
            // addItem() sets both sides of the bidirectional relationship
            // and recalculates the total
            order.addItem(item);
        }

        Order saved = orderRepository.save(order);
        log.info("Order created: id={} correlationId={} total={}", saved.getId(), correlationId, saved.getTotalAmount());

        // Publish OrderCreatedEvent to start the Saga
        // inventory-service will consume this and reserve stock
        List<OrderCreatedEvent.OrderItemEvent> itemEvents = saved.getItems().stream()
                .map(item -> OrderCreatedEvent.OrderItemEvent.builder()
                        .productId(item.getProductId())
                        .sku(item.getSku())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build())
                .collect(Collectors.toList());

        OrderCreatedEvent event = OrderCreatedEvent.of(
                saved.getId(), userId, saved.getTotalAmount(),
                itemEvents, request.shippingAddress(), correlationId
        );
        eventProducer.publishOrderCreated(event);

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        return toDto(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable).map(this::toDto);
    }

    /** Converts Order entity to DTO */
    private OrderDto toDto(Order order) {
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> new OrderItemDto(
                        item.getId(), item.getProductId(), item.getProductName(),
                        item.getSku(), item.getQuantity(), item.getUnitPrice()))
                .collect(Collectors.toList());

        return new OrderDto(
                order.getId(), order.getUserId(), order.getCorrelationId(),
                order.getStatus(), order.getTotalAmount(), order.getShippingAddress(),
                order.getNotes(), itemDtos, order.getCreatedAt(), order.getUpdatedAt()
        );
    }
}
