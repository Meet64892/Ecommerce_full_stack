package com.smartshop.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * OrderServiceApplication - Bootstraps the order lifecycle + Saga service.
 *
 * <h2>Purpose</h2>
 * Owns the order aggregate and coordinates the distributed "place order" flow
 * across inventory (and notionally payment) using the Saga pattern over Kafka.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Saga pattern</b>: a long-running business transaction split into a
 *       series of local transactions, each emitting an event that triggers the
 *       next step — with compensating actions to undo on failure. It replaces a
 *       single distributed ACID transaction (which doesn't scale; see below).</li>
 *   <li><b>Orchestration vs choreography</b>: this service uses ORCHESTRATION —
 *       a central coordinator ({@code SagaOrchestrator}) tells each participant
 *       what to do. Choreography would instead have services react to each
 *       other's events with no central brain.</li>
 *   <li><b>@EnableJpaAuditing</b>: auto-populates the Order's created/updated
 *       timestamps.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Registers as {@code order-service}; gateway routes {@code /api/orders/**}
 * (with a circuit breaker) here. Publishes/consumes Kafka events.
 *
 * @author SmartShop Team
 */
@SpringBootApplication
@EnableJpaAuditing
public class OrderServiceApplication {

    /**
     * Spring Boot entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
