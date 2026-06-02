package com.smartshop.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * ServiceRegistryApplication - Eureka registry bootstrap class.
 *
 * <h2>Purpose</h2>
 * This application hosts the service registry used by all SmartShop microservices for runtime
 * discovery. In distributed systems, static host/port wiring is brittle, so Eureka provides
 * dynamic registration and lookup.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Service discovery: clients query logical service names instead of hardcoded addresses.</li>
 *   <li>Eureka lease model: instances send periodic heartbeats to renew leases.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Every business service registers itself here, and the API gateway resolves routes through Eureka.
 *
 * @see org.springframework.cloud.netflix.eureka.server.EnableEurekaServer
 * @author SmartShop Team
 */
@SpringBootApplication
@EnableEurekaServer
public class ServiceRegistryApplication {

    /**
     * Starts the registry process.
     *
     * @param args startup arguments passed by the JVM launcher
     * @return nothing because SpringApplication.run blocks the main thread
     */
    public static void main(final String[] args) {
        SpringApplication.run(ServiceRegistryApplication.class, args);
    }
}
