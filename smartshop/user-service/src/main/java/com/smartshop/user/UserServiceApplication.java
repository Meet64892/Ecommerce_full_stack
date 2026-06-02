package com.smartshop.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * UserServiceApplication - Starts authentication and user profile management.
 *
 * <h2>Purpose</h2>
 * User identity is a foundational bounded context for an e-commerce platform because orders, inventory actions,
 * and administrative operations all depend on authenticated principals. This service owns credentials and exposes
 * token-based authentication without leaking password data to other services.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>JWT: A signed header.payload.signature token that lets services verify identity without server sessions.</li>
 *   <li>BCrypt: A password hashing algorithm with salt rounds that slows brute-force attacks.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Clients register or log in here, receive a JWT, and then send that token through the API Gateway to other services.
 *
 * @see com.smartshop.user.security.SecurityConfig
 * @author SmartShop Team
 */
@SpringBootApplication
public class UserServiceApplication {
    /**
     * Starts the Spring Boot application.
     *
     * @param args command-line arguments from the runtime
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
