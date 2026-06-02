package com.smartshop.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * ConfigServerApplication - Centralized configuration server.
 *
 * <h2>Purpose</h2>
 * This service externalizes environment-specific configuration from code, aligning with
 * the 12-factor App principle that config should be strictly separated from binaries.
 * It allows changing runtime settings without rebuilding service artifacts.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Native backend: reads local filesystem config files for developer productivity.</li>
 *   <li>Config lookup path: /{application}/{profile} returns merged property sources.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Client services call this server during bootstrap to fetch profile-specific YAML before
 * fully starting their application context.
 *
 * @see org.springframework.cloud.config.server.EnableConfigServer
 * @author SmartShop Team
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    /**
     * Starts the config server process.
     *
     * @param args launcher arguments
     * @return nothing; method delegates to Spring runtime bootstrap
     */
    public static void main(final String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
