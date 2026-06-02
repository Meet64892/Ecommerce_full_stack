package com.smartshop.order.entity;

/**
 * OrderStatus - The lifecycle states an order moves through.
 *
 * <h2>Purpose</h2>
 * Models the order state machine. The Saga advances the status as each step
 * completes (or fails), giving a clear, auditable lifecycle.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Typical transitions: PENDING -> CONFIRMED -> SHIPPED -> DELIVERED, or
 *       PENDING -> CANCELLED if inventory reservation fails (compensation).</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Persisted on {@link Order#getStatus()} and driven by the saga.
 *
 * @author SmartShop Team
 */
public enum OrderStatus {
    /** Created, awaiting inventory confirmation. */
    PENDING,
    /** Inventory reserved; order is confirmed. */
    CONFIRMED,
    /** Handed to fulfillment/shipping. */
    SHIPPED,
    /** Received by the customer. */
    DELIVERED,
    /** Aborted (e.g. insufficient stock) — the saga's compensation outcome. */
    CANCELLED
}
