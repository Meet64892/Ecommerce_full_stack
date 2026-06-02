package com.smartshop.inventory.kafka;

/**
 * InventoryCheckedEvent - Kafka response emitted after stock check.
 *
 * <h2>Purpose</h2>
 * Notifies order-service whether inventory reservation succeeded.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Asynchronous callback: saga progresses based on this event.</li>
 *   <li>Failure reason propagation: supports operational diagnosis.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Produced by inventory consumer and consumed by order-service.
 *
 * @see OrderEventConsumer
 * @author SmartShop Team
 */
public record InventoryCheckedEvent(Long orderId, boolean available, String reason) {
}
