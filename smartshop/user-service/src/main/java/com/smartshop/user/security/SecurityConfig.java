package com.smartshop.user.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig - Spring Security 6 configuration (no WebSecurityConfigurerAdapter).
 *
 * <h2>Purpose</h2>
 * Defines the HTTP security rules: which endpoints are public, that we are
 * stateless (JWT, no sessions), the password hashing scheme, and where the
 * {@link JwtAuthFilter} plugs into the chain.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>SecurityFilterChain bean</b>: Spring Security 6 favors a component
 *       (bean) style over the deprecated adapter class.</li>
 *   <li><b>Stateless sessions</b>: {@code SessionCreationPolicy.STATELESS} —
 *       no JSESSIONID; the token authenticates every request.</li>
 *   <li><b>CSRF disabled</b>: CSRF protection guards cookie/session auth; with
 *       stateless bearer tokens it is unnecessary and would block our API.</li>
 *   <li><b>BCryptPasswordEncoder</b>: adaptive hashing with per-password salt;
 *       the work factor makes brute force expensive.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * The authentication entry point for the whole service; the JWT filter it
 * registers sets the principal that {@code @PreAuthorize}/role checks rely on.
 *
 * @see JwtAuthFilter
 * @author SmartShop Team
 */
@Configuration
// Enables method-level security so we can annotate endpoints with @PreAuthorize.
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Builds the HTTP security filter chain.
     *
     * @param http the HttpSecurity builder
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if the chain cannot be built
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF: irrelevant for stateless token auth.
                .csrf(AbstractHttpConfigurer::disable)
                // Public vs protected endpoints.
                .authorizeHttpRequests(auth -> auth
                        // Auth + docs + health are open to everyone.
                        .requestMatchers(
                                "/auth/register",
                                "/auth/login",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/**").permitAll()
                        // Everything else requires a valid token.
                        .anyRequest().authenticated())
                // No server-side session — pure JWT.
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                // Run our JWT filter before the form-login username/password filter.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * The password hashing strategy used to store and verify passwords.
     *
     * @return a BCrypt encoder (default strength 10 rounds)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Wires our {@link UserDetailsServiceImpl} + encoder into an authentication
     * provider that the manager uses to verify login credentials.
     *
     * @return the configured DAO authentication provider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Exposes the {@link AuthenticationManager} so the login flow can trigger
     * credential verification.
     *
     * @param config the Spring-provided authentication configuration
     * @return the application's authentication manager
     * @throws Exception if it cannot be obtained
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
