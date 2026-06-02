package com.smartshop.user.security;

import com.smartshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * UserDetailsServiceImpl - Spring Security principal lookup adapter.
 *
 * <h2>Purpose</h2>
 * Bridges domain User records into Spring Security's authentication model.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>UserDetailsService: core contract used by DaoAuthenticationProvider.</li>
 *   <li>Authority mapping: role is converted to GrantedAuthority.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Authentication manager calls this class during login credential verification.
 *
 * @see SecurityConfig
 * @author SmartShop Team
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads user credentials by email.
     *
     * @param username email subject
     * @return spring-security user details
     * @throws UsernameNotFoundException when email does not exist
     */
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final var user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                .build();
    }
}
