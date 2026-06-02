package com.smartshop.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * ConfigServerApplication - Centralized configuration server.
 *
 * <h2>Purpose</h2>
 * Implements the 12-Factor App principle III ("store config in the
 * environment"): configuration lives <i>outside</i> the deployable artifact so
 * the same JAR runs unchanged in dev/staging/prod, with only the served config
 * differing. This server hands each service its config at startup.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Resolution by {application}/{profile}/{label}</b>: a request to
 *       {@code /user-service/dev} returns {@code user-service-dev.yml} merged
 *       over {@code user-service.yml} and {@code application.yml} (most specific
 *       wins). This is how one server serves every service and environment.</li>
 *   <li><b>Backends</b>: config can come from a Git repo (versioned, auditable)
 *       or — as here for simplicity — the local filesystem via the "native"
 *       profile.</li>
 *   <li><b>Fail-fast vs fail-safe</b>: clients can be told to refuse to start if
 *       the config server is unreachable (fail-fast, safest for prod) or to fall
 *       back to local config (fail-safe). See each client's config import
 *       setting (optional: vs required).</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Boots before the business services; each service's {@code application.yml}
 * imports config from this server using {@code spring.config.import}.
 *
 * @author SmartShop Team
 */
@SpringBootApplication
// @EnableConfigServer turns this app into a configuration HTTP endpoint that
// serves files from the configured backend repository.
@EnableConfigServer
public class ConfigServerApplication {

    /**
     * Spring Boot entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
