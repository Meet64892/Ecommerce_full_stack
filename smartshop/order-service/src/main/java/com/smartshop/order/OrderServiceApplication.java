package com.smartshop.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * OrderServiceApplication - Order Management and Saga Orchestration Service
 *
 * <h2>Purpose</h2>
 * Manages the complete order lifecycle from PENDING to DELIVERED.
 * Implements the Saga pattern to coordinate distributed transactions across
 * inventory-service, and notification-service via Kafka events.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Saga Pattern: A sequence of local transactions where each step publishes
 *       an event that triggers the next step. If any step fails, compensating
 *       transactions undo previous steps.
 *       Example: place-order → reserve-inventory → process-payment → confirm-order
 *       If reserve-inventory fails → cancel-order (compensating transaction).</li>
 *   <li>Orchestration vs Choreography:
 *       Orchestration (this service): A central orchestrator (SagaOrchestrator) directs
 *       each step explicitly. Easier to understand and debug but creates coupling.
 *       Choreography: Services react to events independently. More decoupled but
 *       harder to trace the overall flow.</li>
 *   <li>Eventual Consistency: The order might be PENDING for seconds while
 *       inventory is being checked and payment processed. This is acceptable —
 *       the system will reach a consistent state eventually.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
