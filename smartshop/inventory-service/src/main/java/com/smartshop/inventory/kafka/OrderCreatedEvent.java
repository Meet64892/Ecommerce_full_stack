package com.smartshop.inventory.kafka;

import java.math.BigDecimal;

/**
 * OrderCreatedEvent - Simplified order.created payload for inventory service.
 *
 * <h2>Purpose</h2>
 * Represents order event fields needed by inventory to reserve stock.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Contract projection: consumer can deserialize only relevant event fields.</li>
 *   <li>Decoupling: avoids compile-time dependency on producer module internals.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Consumed from Kafka order.created topic.
 *
 * @see OrderEventConsumer
 * @author SmartShop Team
 */
public record OrderCreatedEvent(Long orderId, Long userId, BigDecimal totalAmount) {
}
