package com.smartshop.user.security;

import com.smartshop.user.entity.User;
import com.smartshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserDetailsServiceImpl - Bridges our {@link User} entity to Spring Security.
 *
 * <h2>Purpose</h2>
 * Spring Security needs a way to load a principal by username during
 * authentication. This adapter fetches our entity and wraps it in the framework's
 * {@link UserDetails} contract (username, password hash, authorities).
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>UserDetailsService</b>: the single method the auth manager calls to
 *       load a user; throwing {@link UsernameNotFoundException} signals "no such
 *       user" (which the manager reports as bad credentials to avoid enumeration).</li>
 *   <li><b>Authorities</b>: roles are exposed as {@code ROLE_<NAME>} so
 *       {@code hasRole("ADMIN")} checks line up with our enum.</li>
 *   <li><b>Constructor injection</b> (via Lombok {@code @RequiredArgsConstructor}):
 *       dependencies are final and explicit, which makes the class trivially
 *       testable and immutable.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Wired into the {@code AuthenticationProvider} in {@code SecurityConfig} and
 * used by {@code JwtAuthFilter} to rebuild the security context per request.
 *
 * @author SmartShop Team
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user by email for authentication.
     *
     * @param username the email used as the principal name
     * @return a Spring Security {@link UserDetails} view of the user
     * @throws UsernameNotFoundException if no user has that email
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Build the framework principal. We use the standard User builder rather
        // than implementing UserDetails on our entity, keeping the persistence
        // model decoupled from the security model.
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .disabled(!user.isEnabled())
                .build();
    }
}
