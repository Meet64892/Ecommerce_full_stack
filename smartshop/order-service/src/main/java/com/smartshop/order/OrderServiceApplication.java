package com.smartshop.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OrderServiceApplication - Starts order placement and Saga orchestration.
 *
 * <h2>Purpose</h2>
 * Order-service owns checkout state and coordinates asynchronous steps that span inventory, payment, and notification
 * boundaries. It demonstrates how microservices avoid global database transactions while still completing business flows.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Saga orchestration: One service coordinates local transactions and compensating actions.</li>
 *   <li>Eventual consistency: Other services catch up through Kafka events rather than one immediate commit.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Clients create orders through the gateway, this service publishes Kafka events, and inventory replies asynchronously.
 *
 * @see com.smartshop.order.service.SagaOrchestrator
 * @author SmartShop Team
 */
@SpringBootApplication
public class OrderServiceApplication {
    /**
     * Starts the order service.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
