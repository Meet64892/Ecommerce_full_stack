package com.smartshop.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * ServiceRegistryApplication - Eureka Service Discovery Server
 *
 * <h2>Purpose</h2>
 * In a microservices architecture, services need to communicate with each other.
 * Without service discovery, you must hardcode host:port addresses in config files.
 * This breaks in cloud environments where instances get dynamic IP addresses and
 * scale up/down automatically. The service registry solves this by acting as a
 * "phone book" — services register their current address on startup, and other
 * services look them up by name at runtime.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Service Discovery: The pattern of automatically detecting and tracking
 *       available service instances. Two approaches exist:
 *       Client-side discovery (Eureka/Ribbon): client queries registry and picks instance.
 *       Server-side discovery (AWS ALB, Kubernetes): load balancer handles routing.</li>
 *   <li>Heartbeat Mechanism: Every registered service sends a "heartbeat" HTTP request
 *       to Eureka every 30 seconds (default). If Eureka doesn't receive a heartbeat
 *       within 90 seconds (3 missed heartbeats), it removes the instance from the
 *       registry, preventing traffic from being routed to dead instances.</li>
 *   <li>Lease Renewal: The act of sending a heartbeat is called "lease renewal".
 *       Each registration creates a "lease" that expires unless renewed.</li>
 *   <li>Self-Preservation Mode: When Eureka loses many heartbeats simultaneously
 *       (network partition scenario), it assumes the network is faulty rather than
 *       all instances being dead, and stops evicting registrations. This prevents
 *       mass deregistration during temporary network blips.</li>
 *   <li>@EnableEurekaServer: This single annotation activates all Eureka server
 *       infrastructure — the REST API for registrations, the dashboard at /eureka/,
 *       the heartbeat processing, and the registry replication logic.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * This starts FIRST (before any other service). All microservices (user-service,
 * product-service, etc.) have spring-cloud-starter-netflix-eureka-client on their
 * classpath, which causes them to register with this server on startup using the
 * eureka.client.service-url.defaultZone property in their application.yml.
 *
 * @see <a href="http://localhost:8761">Eureka Dashboard (when running)</a>
 * @author SmartShop Team
 */
@SpringBootApplication
// @EnableEurekaServer activates the Eureka server auto-configuration.
// Without this annotation, the application would start as a plain Spring Boot app
// with no service registry functionality despite having the dependency on the classpath.
@EnableEurekaServer
public class ServiceRegistryApplication {

    /**
     * Application entry point. SpringApplication.run() bootstraps the Spring
     * ApplicationContext, which triggers auto-configuration, component scanning,
     * and starts the embedded Tomcat server on port 8761.
     *
     * @param args command-line arguments (e.g., --spring.profiles.active=prod)
     */
    public static void main(String[] args) {
        SpringApplication.run(ServiceRegistryApplication.class, args);
    }
}
