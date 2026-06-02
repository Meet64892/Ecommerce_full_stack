package com.smartshop.order.entity;

/**
 * OrderStatus - Order lifecycle state machine.
 *
 * <h2>Purpose</h2>
 * Captures business progression from initial creation through fulfillment or cancellation.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Explicit state modeling: avoids ambiguous boolean flag combinations.</li>
 *   <li>Workflow tracing: status changes can be audited and replayed.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Order entity persists this enum and saga transitions it during event processing.
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
