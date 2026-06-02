package com.smartshop.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * ServiceRegistryApplication - The Eureka service-discovery server.
 *
 * <h2>Purpose</h2>
 * In a microservices system, service instances come and go (autoscaling,
 * crashes, redeploys) and their network locations (host:port) are dynamic. A
 * <b>service registry</b> is the phone book that lets a service find another by
 * <i>logical name</i> ("user-service") instead of a hard-coded IP. This class
 * boots the Eureka server that hosts that registry.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Service discovery</b>: clients ask the registry "where is X?" and get
 *       back the current list of healthy instances, then load-balance across them.</li>
 *   <li><b>Heartbeat &amp; lease renewal</b>: each client sends a heartbeat every
 *       30s by default to renew its lease. Eureka tracks the last renewal time.</li>
 *   <li><b>Eviction</b>: if a client misses heartbeats past the lease-expiration
 *       window, Eureka evicts it so traffic stops being routed to a dead node.</li>
 *   <li><b>Self-preservation</b>: if too many heartbeats are lost at once (likely
 *       a network blip, not mass death), Eureka stops evicting to avoid wiping a
 *       still-healthy registry.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Every other service (except this one) runs a Eureka <i>client</i> that
 * registers here on startup and queries here to locate peers. The API gateway
 * uses these registrations to route by service id.
 *
 * @author SmartShop Team
 */
@SpringBootApplication
// @EnableEurekaServer flips this Spring Boot app into a Eureka registry server,
// wiring up the registry data structures and the dashboard at the root URL.
@EnableEurekaServer
public class ServiceRegistryApplication {

    /**
     * Standard Spring Boot entry point.
     *
     * @param args command-line arguments forwarded to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(ServiceRegistryApplication.class, args);
    }
}
