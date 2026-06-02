package com.smartshop.order.entity;

/**
 * OrderStatus - State Machine for the Order Lifecycle
 *
 * <h2>Purpose</h2>
 * Defines valid states and the allowed transitions for an order.
 * Using an enum prevents invalid state assignments at compile time.
 *
 * <h2>Order State Machine</h2>
 * <pre>
 * PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED
 *    ↓           ↓
 * CANCELLED   CANCELLED
 * </pre>
 *
 * @author SmartShop Team
 */
public enum OrderStatus {
    /**
     * Initial state: order created, awaiting inventory check and payment.
     * The Saga begins in this state.
     */
    PENDING,

    /**
     * Inventory reserved and payment authorized.
     * Moved here by SagaOrchestrator after successful inventory check.
     */
    CONFIRMED,

    /**
     * Warehouse is preparing the order for shipment.
     */
    PROCESSING,

    /**
     * Package handed to shipping carrier. Tracking number available.
     */
    SHIPPED,

    /**
     * Customer received the package. Order lifecycle complete.
     */
    DELIVERED,

    /**
     * Order cancelled (either by customer, or compensating transaction on failure).
     * Terminal state — cancelled orders cannot be reactivated.
     */
    CANCELLED
}
