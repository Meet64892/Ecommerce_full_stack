package com.smartshop.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * UserServiceApplication - Authentication and user profile service entry point.
 *
 * <h2>Purpose</h2>
 * This service owns user identity, registration, login, and profile management. Keeping identity
 * concerns isolated prevents accidental coupling between authentication and other domains.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Bounded context: user identity rules are centralized in one service.</li>
 *   <li>Stateless auth: JWT tokens allow scaling without sticky sessions.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * API gateway forwards auth calls here; other services consume token claims for authorization.
 *
 * @see com.smartshop.user.security.SecurityConfig
 * @author SmartShop Team
 */
@SpringBootApplication
public class UserServiceApplication {

    /**
     * Starts the user service application context.
     *
     * @param args runtime startup arguments
     * @return nothing because Spring bootstraps and blocks process thread
     */
    public static void main(final String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
