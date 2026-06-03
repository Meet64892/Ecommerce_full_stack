package com.smartshop.order.service;

import com.smartshop.common.exception.ResourceNotFoundException;
import com.smartshop.common.exception.ValidationException;
import com.smartshop.order.dto.CreateOrderRequest;
import com.smartshop.order.dto.OrderDto;
import com.smartshop.order.dto.OrderItemDto;
import com.smartshop.order.entity.Order;
import com.smartshop.order.entity.OrderItem;
import com.smartshop.order.entity.OrderStatus;
import com.smartshop.order.repository.OrderRepository;
import com.smartshop.order.security.MarketplaceContext;
import com.smartshop.order.security.MarketplacePrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * OrderServiceImpl - Implements checkout and order history use cases.
 */
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final SagaOrchestrator sagaOrchestrator;

    public OrderServiceImpl(OrderRepository orderRepository, SagaOrchestrator sagaOrchestrator) {
        this.orderRepository = orderRepository;
        this.sagaOrchestrator = sagaOrchestrator;
    }

    @Override
    @Transactional
    public OrderDto createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setUserId(request.userId());
        request.items().forEach(item -> {
            OrderItem line = new OrderItem(item.productId(), item.quantity(), item.unitPrice());
            line.setBrandId(item.brandId());
            line.setProductName(item.productName());
            order.addItem(line);
        });
        Order saved = orderRepository.save(order);
        sagaOrchestrator.start(saved);
        return toDto(saved, null);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrder(UUID id) {
        return orderRepository.findById(id).map(o -> toDto(o, null)).orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> ordersForUser(UUID userId) {
        return orderRepository.findByUserId(userId).stream().map(o -> toDto(o, null)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> listAllOrders(Pageable pageable) {
        requireSuperAdmin();
        return orderRepository.findAllByOrderByCreatedAtDesc(pageable).map(o -> toDto(o, null));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> listOrdersForBrand(UUID brandId, Pageable pageable) {
        return orderRepository.findDistinctByBrandId(brandId, pageable).map(o -> toDto(o, brandId));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderForBrand(UUID orderId, UUID brandId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        if (!orderContainsBrand(order, brandId)) {
            throw new ResourceNotFoundException("Order", orderId);
        }
        return toDto(order, brandId);
    }

    @Override
    @Transactional
    public OrderDto updateStatusAsAdmin(UUID orderId, OrderStatus status) {
        requireSuperAdmin();
        return updateStatus(orderId, status, null);
    }

    @Override
    @Transactional
    public OrderDto updateStatusAsVendor(UUID orderId, UUID brandId, OrderStatus status) {
        if (brandId == null) {
            throw new ValidationException("Vendor brand required");
        }
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        if (!orderContainsBrand(order, brandId)) {
            throw new ResourceNotFoundException("Order", orderId);
        }
        validateVendorStatusTransition(status);
        return updateStatus(orderId, status, brandId);
    }

    private OrderDto updateStatus(UUID orderId, OrderStatus status, UUID brandFilter) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        order.setStatus(status);
        return toDto(order, brandFilter);
    }

    private void validateVendorStatusTransition(OrderStatus status) {
        if (status == OrderStatus.CANCELLED) {
            throw new ValidationException("Vendors cannot cancel orders — contact platform support");
        }
    }

    private static boolean orderContainsBrand(Order order, UUID brandId) {
        return order.getItems().stream().anyMatch(item -> brandId.equals(item.getBrandId()));
    }

    private static MarketplacePrincipal requireSuperAdmin() {
        MarketplacePrincipal principal = MarketplaceContext.get();
        if (principal == null || !principal.isSuperAdmin()) {
            throw new ValidationException("Super Admin access required");
        }
        return principal;
    }

    private OrderDto toDto(Order order, UUID brandFilter) {
        List<OrderItemDto> items = order.getItems().stream()
                .filter(item -> brandFilter == null || brandFilter.equals(item.getBrandId()))
                .map(item -> new OrderItemDto(
                        item.getId(),
                        item.getProductId(),
                        item.getBrandId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice()))
                .toList();
        BigDecimal total = items.stream()
                .map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal displayTotal = brandFilter != null ? total : order.getTotalAmount();
        return new OrderDto(order.getId(), order.getUserId(), order.getStatus(), displayTotal, items, order.getCreatedAt());
    }
}
