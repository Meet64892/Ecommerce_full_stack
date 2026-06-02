package com.smartshop.user.security;

import com.smartshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UserDetailsServiceImpl - Bridges Spring Security and Our User Repository
 *
 * <h2>Purpose</h2>
 * Spring Security's authentication mechanism needs to load user details (credentials,
 * authorities) during authentication. UserDetailsService is the interface that Spring
 * Security calls to load a user by username. We implement it to load from our PostgreSQL
 * database via UserRepository.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>UserDetailsService: Spring Security's abstraction for user loading.
 *       By implementing it, we tell Spring Security: "to find a user, call MY loadUserByUsername()
 *       method." Spring calls this during UsernamePasswordAuthenticationToken validation.</li>
 *   <li>UserDetails: Spring Security's representation of a user.
 *       Contains: username, password (hashed), authorities (roles), and account status flags.
 *       We wrap our User entity in Spring's built-in User class (which implements UserDetails).</li>
 *   <li>GrantedAuthority: Spring Security's representation of a permission/role.
 *       SimpleGrantedAuthority("ROLE_CUSTOMER") maps to hasRole("CUSTOMER") in security config.
 *       Spring Security requires the "ROLE_" prefix when using hasRole().</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads user details by email (used as the "username" in our system).
     * Called by Spring Security's authentication manager during login.
     * Also called by JwtAuthFilter to load the user for token validation.
     *
     * @param email the email address to look up (we use email as the login identifier)
     * @return UserDetails containing credentials and authorities for Spring Security
     * @throws UsernameNotFoundException if no user with the given email exists
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user details for email: {}", email);

        com.smartshop.user.entity.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        // Grant authority "ROLE_[ROLE_NAME]" — Spring's convention
        // hasRole("CUSTOMER") checks for authority "ROLE_CUSTOMER"
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        // org.springframework.security.core.userdetails.User (Spring's built-in UserDetails)
        // We wrap our User entity data into Spring's UserDetails structure
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())         // We use email as the Spring Security "username"
                .password(user.getPasswordHash())  // BCrypt hash — Spring will call matches() on it
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isEnabled())       // Maps our enabled flag to Spring's disabled
                .build();
    }
}
