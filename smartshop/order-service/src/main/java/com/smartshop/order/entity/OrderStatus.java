package com.smartshop.order.entity;

/**
 * OrderStatus - Lifecycle states for an order.
 *
 * <h2>Purpose</h2>
 * Explicit states make Saga progress observable and prevent ambiguous string values. State transitions drive events,
 * notifications, and compensating actions.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>PENDING: Order is created but dependent steps are not complete.</li>
 *   <li>CANCELLED: A compensating result when inventory or another step fails.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Order entity stores this enum and SagaOrchestrator updates it after inventory responses.
 *
 * @see Order
 * @author SmartShop Team
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
