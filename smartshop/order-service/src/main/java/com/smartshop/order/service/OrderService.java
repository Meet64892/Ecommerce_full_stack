package com.smartshop.order.service;

import com.smartshop.order.dto.CreateOrderRequest;
import com.smartshop.order.dto.OrderDto;
import com.smartshop.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderDto createOrder(CreateOrderRequest request);

    OrderDto getOrder(UUID id);

    List<OrderDto> ordersForUser(UUID userId);

    Page<OrderDto> listAllOrders(Pageable pageable);

    Page<OrderDto> listOrdersForBrand(UUID brandId, Pageable pageable);

    OrderDto getOrderForBrand(UUID orderId, UUID brandId);

    OrderDto updateStatusAsAdmin(UUID orderId, OrderStatus status);

    OrderDto updateStatusAsVendor(UUID orderId, UUID brandId, OrderStatus status);
}
