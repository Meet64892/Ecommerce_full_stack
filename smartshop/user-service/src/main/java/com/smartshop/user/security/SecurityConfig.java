package com.smartshop.user.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig - Spring Security 6 Configuration for User Service
 *
 * <h2>Purpose</h2>
 * Configures Spring Security's filter chain for the user service.
 * Spring Security 6 uses a lambda-based API (replaced the old method-chaining API).
 * Key configurations: which endpoints need authentication, JWT filter placement,
 * stateless session management, and password encoding.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>SecurityFilterChain (Spring Security 6): The modern way to configure security.
 *       Replaces WebSecurityConfigurerAdapter (deprecated and removed in Spring Security 6).
 *       You define a @Bean of type SecurityFilterChain instead of extending WebSecurityConfigurerAdapter.</li>
 *   <li>Stateless Session Management: With JWT, the server never creates HTTP sessions.
 *       SessionCreationPolicy.STATELESS tells Spring Security to never create or use sessions.
 *       This is critical for scalability: any server instance can handle any request
 *       because all state is in the JWT, not the server's session store.</li>
 *   <li>AuthenticationProvider: How Spring Security validates credentials.
 *       DaoAuthenticationProvider uses our UserDetailsService to load the user
 *       and our PasswordEncoder to verify the password.</li>
 *   <li>@EnableMethodSecurity: Enables @PreAuthorize and @PostAuthorize on methods.
 *       Example: @PreAuthorize("hasRole('ADMIN')") on a controller method.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Enables @PreAuthorize, @PostAuthorize, @Secured
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Defines the HTTP security rules and filter chain.
     * The lambda DSL reads: "configure WHAT security rules apply to WHICH URLs".
     *
     * @param http the HttpSecurity builder
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF: stateless JWT APIs don't need CSRF protection
                // CSRF attacks exploit the browser's automatic cookie sending behavior.
                // With JWT in Authorization headers (not cookies), CSRF is irrelevant.
                .csrf(AbstractHttpConfigurer::disable)

                // Configure endpoint authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Open endpoints — no authentication required
                        .requestMatchers(
                                "/auth/register",
                                "/auth/login",
                                "/actuator/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // Admin-only endpoint
                        .requestMatchers("/users").hasRole("ADMIN")
                        // All other requests require any authenticated user
                        .anyRequest().authenticated()
                )

                // Stateless: no server-side sessions; authentication state is in the JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Set our DaoAuthenticationProvider as the authentication provider
                .authenticationProvider(authenticationProvider())

                // Add JwtAuthFilter BEFORE UsernamePasswordAuthenticationFilter.
                // The filter chain processes in order. JwtAuthFilter sets up the
                // SecurityContext, then UsernamePasswordAuthenticationFilter can
                // see that the user is already authenticated and skip its own logic.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCrypt password encoder with the default strength factor of 10.
     * BCrypt strength factor doubles the computation time per increment:
     * - Factor 10: ~100ms per hash (current recommended default)
     * - Factor 12: ~400ms per hash (recommended when hardware allows)
     * - Factor 6: ~10ms per hash (too fast — easier to brute force)
     * The encoder is a @Bean so it can be injected into UserServiceImpl
     * for password hashing during registration.
     *
     * @return BCryptPasswordEncoder with strength=10
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // strength=10 means 2^10 = 1024 iterations of the key derivation
        return new BCryptPasswordEncoder(10);
    }

    /**
     * Configures how Spring Security validates username/password combinations.
     * DaoAuthenticationProvider:
     *   1. Calls userDetailsService.loadUserByUsername(email) to get stored hash
     *   2. Calls passwordEncoder.matches(submittedPassword, storedHash) to verify
     *
     * @return the configured DaoAuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Exposes the AuthenticationManager as a Spring bean so it can be injected
     * wherever we need to programmatically authenticate (e.g., in UserServiceImpl).
     * AuthenticationConfiguration provides the auto-configured AuthenticationManager.
     *
     * @param config Spring's authentication configuration auto-configuration
     * @return the AuthenticationManager
     * @throws Exception if the manager can't be retrieved
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
