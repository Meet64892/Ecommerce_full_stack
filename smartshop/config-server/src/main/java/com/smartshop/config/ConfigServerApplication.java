package com.smartshop.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * ConfigServerApplication - Serves centralized configuration to SmartShop services.
 *
 * <h2>Purpose</h2>
 * The twelve-factor app methodology says config should live outside code so the same artifact can run in
 * dev, staging, and production. Spring Cloud Config Server exposes versioned configuration by application
 * name and profile, which keeps service jars immutable.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Native backend: A local folder acts like a config repository for this educational project.</li>
 *   <li>Fail-fast vs fail-safe: Clients may stop on missing config or continue with local defaults.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Services request `/application/profile` documents from this server during bootstrap and merge those values
 * with their local application.yml files.
 *
 * @see EnableConfigServer
 * @author SmartShop Team
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    /**
     * Starts the Config Server web process.
     *
     * @param args command-line arguments from the process launcher
     */
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
