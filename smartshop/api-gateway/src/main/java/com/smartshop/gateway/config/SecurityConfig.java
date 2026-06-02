package com.smartshop.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * SecurityConfig - Spring Security Configuration for the Reactive Gateway
 *
 * <h2>Purpose</h2>
 * The API Gateway handles JWT validation via AuthenticationFilter (GatewayFilter),
 * NOT via Spring Security's built-in mechanisms. We still need a SecurityConfig to:
 *   1. Disable CSRF (Cross-Site Request Forgery protection) — not needed for stateless JWT APIs
 *   2. Permit all paths at the Spring Security level (let the gateway filters handle auth)
 *   3. Disable form-based login (we use JWT, not session-based auth)
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>CSRF Protection: Designed for session-based authentication where a browser
 *       automatically sends session cookies. With JWT in Authorization headers,
 *       CSRF is not a concern — a malicious site can't read the JWT from localStorage.</li>
 *   <li>@EnableWebFluxSecurity vs @EnableWebSecurity: Use WebFlux variant for reactive
 *       applications (WebFlux/Gateway). Use the regular variant for servlet applications.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Configures Spring Security to allow all requests through at the framework level,
     * delegating actual authentication to our custom AuthenticationFilter.
     * This prevents double-checking and allows the GatewayFilter chain to own auth.
     *
     * @param http the reactive ServerHttpSecurity to configure
     * @return the configured SecurityWebFilterChain
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                // Disable CSRF: not needed for stateless JWT REST APIs
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // Disable form login: we use JWT, not username/password form submission
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                // Disable HTTP Basic auth: replaced by Bearer token auth
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                // Permit all at Spring Security level — our GatewayFilter handles auth
                .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
                .build();
    }
}
