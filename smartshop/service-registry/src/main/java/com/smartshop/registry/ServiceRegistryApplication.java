package com.smartshop.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * ServiceRegistryApplication - Starts the Eureka service discovery server.
 *
 * <h2>Purpose</h2>
 * Microservices are often scaled horizontally and moved between hosts, so callers should not hard-code
 * hostnames. Eureka gives services a registry where instances publish their network locations and clients
 * discover them by logical service id.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Heartbeat: Clients renew leases periodically so Eureka knows they are alive.</li>
 *   <li>Eviction: Instances that stop renewing leases are eventually removed from the registry.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Every service except this server registers with Eureka, and the gateway uses those registrations to route
 * traffic to `lb://service-name` destinations.
 *
 * @see EnableEurekaServer
 * @author SmartShop Team
 */
@SpringBootApplication
@EnableEurekaServer
public class ServiceRegistryApplication {
    /**
     * Boots the Eureka server and lets Spring create the embedded web container.
     *
     * @param args command-line arguments passed through by the runtime
     */
    public static void main(String[] args) {
        SpringApplication.run(ServiceRegistryApplication.class, args);
    }
}
