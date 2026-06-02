package com.smartshop.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * ConfigServerApplication - Centralized Configuration Management Server
 *
 * <h2>Purpose</h2>
 * The 12-factor app methodology (a set of best practices for building cloud-native
 * applications) states Principle III: "Store config in the environment" — configuration
 * that varies between deployments (dev, staging, prod) should never be baked into
 * the application code. Spring Cloud Config Server implements this by serving
 * externalized configuration to all microservices from a central location.
 *
 * Without a config server, changing a database URL or API key requires:
 *   1. Editing the property in each service's application.yml
 *   2. Rebuilding each service JAR
 *   3. Redeploying each service
 *
 * With a config server:
 *   1. Edit the property in one config-repo file
 *   2. Services refresh their config via /actuator/refresh (or Spring Cloud Bus for automatic refresh)
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Config Resolution by {application}/{profile}: A service named "user-service"
 *       with profile "prod" will request config from:
 *       GET /user-service/prod → config-server serves user-service-prod.yml</li>
 *   <li>Native Profile vs Git Backend: Config files can be stored in a local folder
 *       (native profile, used here for simplicity) or in a Git repository (recommended
 *       for production). Git gives you version history, rollback capability, and
 *       PR-based config change reviews.</li>
 *   <li>Fail-Fast vs Fail-Safe: If config-server is down when a service starts,
 *       the service can either fail immediately (fail-fast: good for production, ensures
 *       services don't start with missing config) or fall back to local config
 *       (fail-safe: good for development, allows starting without config server).</li>
 *   <li>Config Encryption: The config server can encrypt sensitive values so they're
 *       stored as {cipher}EncryptedBase64String in config files and decrypted before
 *       serving. This prevents secrets from being readable in the Git repository.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Starts second (after service-registry). All services have spring-cloud-starter-config
 * which adds a bootstrap phase: before the application context starts, the service
 * fetches its configuration from this server and merges it with local properties.
 *
 * @author SmartShop Team
 */
@SpringBootApplication
// @EnableConfigServer activates the config server auto-configuration.
// This sets up the REST endpoints, backend configuration (Git, native, Vault),
// and property source resolution logic.
@EnableConfigServer
// Register this server with Eureka so services can discover it by name
// "config-server" rather than hardcoded URL
@EnableDiscoveryClient
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
