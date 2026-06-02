package com.smartshop.user.security;

import com.smartshop.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * UserDetailsServiceImpl - Adapts SmartShop users to Spring Security principals.
 *
 * <h2>Purpose</h2>
 * Spring Security authenticates against UserDetails rather than domain entities. This adapter loads the user record
 * and exposes only the credential hash, username, enabled flag, and authorities needed by the filter chain.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Constructor injection: Dependencies are final, explicit, and easy to replace in tests.</li>
 *   <li>Authority mapping: Roles become `ROLE_*` authorities understood by Spring Security.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * AuthenticationManager and JwtAuthFilter call this service whenever credentials or tokens need user details.
 *
 * @see com.smartshop.user.security.SecurityConfig
 * @author SmartShop Team
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Creates the adapter with the repository it needs to load users.
     *
     * @param userRepository persistence access for user records
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by email for authentication or JWT validation.
     *
     * @param username email address used as the login username
     * @return Spring Security UserDetails instance
     * @throws UsernameNotFoundException when the email does not exist
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPasswordHash())
                        .disabled(!user.isEnabled())
                        .roles(user.getRole().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
