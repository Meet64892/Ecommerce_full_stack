package com.smartshop.user.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig - Defines Spring Security 6 authentication behavior.
 *
 * <h2>Purpose</h2>
 * Security is configured with beans rather than the removed WebSecurityConfigurerAdapter. Stateless JWT sessions keep
 * the service horizontally scalable because authentication state lives in signed tokens rather than server memory.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Filter chain: Ordered security filters process authentication, authorization, and exception handling.</li>
 *   <li>BCrypt: Passwords are salted and hashed; increasing work factor slows offline cracking attempts.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Auth endpoints stay public, other endpoints require authentication, and JwtAuthFilter supplies the principal.
 *
 * @see JwtAuthFilter
 * @author SmartShop Team
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Creates the security configuration with its custom JWT filter.
     *
     * @param jwtAuthFilter filter that validates bearer tokens
     */
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Defines endpoint authorization and inserts the JWT filter before username/password authentication.
     *
     * @param http mutable HttpSecurity builder supplied by Spring Security
     * @return immutable SecurityFilterChain used for requests
     * @throws Exception when Spring Security cannot build the filter chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/actuator/health", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/brands", "/brands/*").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Exposes the AuthenticationManager built from UserDetailsService and PasswordEncoder beans.
     *
     * @param configuration Spring Security authentication configuration
     * @return authentication manager for login attempts
     * @throws Exception when the manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Creates the password encoder used to hash and verify passwords.
     *
     * @return BCrypt encoder with framework defaults
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
