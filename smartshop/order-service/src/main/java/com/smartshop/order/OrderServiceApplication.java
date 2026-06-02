package com.smartshop.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OrderServiceApplication - Order orchestration service entry point.
 *
 * <h2>Purpose</h2>
 * Owns order lifecycle and orchestrates distributed workflows with inventory and notification
 * components through asynchronous events.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Saga orchestration: coordinator drives multi-service business transaction steps.</li>
 *   <li>Eventual consistency: state converges asynchronously across service boundaries.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Receives /orders requests, persists state, emits events, and reacts to inventory responses.
 *
 * @see com.smartshop.order.service.SagaOrchestrator
 * @author SmartShop Team
 */
@SpringBootApplication
public class OrderServiceApplication {

    /**
     * Starts order service.
     *
     * @param args startup args
     * @return nothing
     */
    public static void main(final String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
