package com.smartshop.inventory.kafka;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * OrderItemMessage - Kafka line-item representation consumed by inventory-service.
 *
 * <h2>Purpose</h2>
 * Inventory-service needs only product id and quantity to reserve stock, but keeping unit price allows the JSON shape
 * to match order-service events. This local message type avoids a compile-time dependency on order-service.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Service autonomy: Services share event contracts, not internal code modules.</li>
 *   <li>Deserialization DTO: Kafka payloads are mapped into simple Java objects.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * OrderEventConsumer reads OrderCreatedEvent messages containing these items.
 *
 * @see OrderCreatedEvent
 * @author SmartShop Team
 */
public class OrderItemMessage {
    private UUID id;
    private UUID productId;
    private int quantity;
    private BigDecimal unitPrice;
    /** Required by Jackson. */ public OrderItemMessage() {}
    /** @return line id */ public UUID getId() { return id; }
    /** @param id line id */ public void setId(UUID id) { this.id = id; }
    /** @return product id */ public UUID getProductId() { return productId; }
    /** @param productId product id */ public void setProductId(UUID productId) { this.productId = productId; }
    /** @return quantity */ public int getQuantity() { return quantity; }
    /** @param quantity quantity */ public void setQuantity(int quantity) { this.quantity = quantity; }
    /** @return unit price */ public BigDecimal getUnitPrice() { return unitPrice; }
    /** @param unitPrice unit price */ public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
