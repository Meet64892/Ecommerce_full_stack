package com.smartshop.user.service;

import com.smartshop.user.dto.AuthResponse;
import com.smartshop.user.dto.LoginRequest;
import com.smartshop.user.dto.RegisterRequest;
import com.smartshop.user.dto.UserDto;
import com.smartshop.user.entity.Role;
import com.smartshop.user.entity.User;
import com.smartshop.user.exception.EmailAlreadyExistsException;
import com.smartshop.user.exception.UserNotFoundException;
import com.smartshop.user.mapper.UserMapper;
import com.smartshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UserServiceImpl - Core implementation of authentication & user management.
 *
 * <h2>Purpose</h2>
 * Implements registration (hashing passwords, rejecting duplicates), login
 * (verifying credentials, issuing JWTs), and profile reads/updates.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Constructor injection</b> (via Lombok): all collaborators are
 *       {@code final} and provided at construction — explicit dependencies,
 *       immutable, and easy to unit-test (no reflection/field injection).</li>
 *   <li><b>@Transactional</b>: wraps a method in a DB transaction (ACID). On a
 *       {@code RuntimeException} the transaction rolls back, so a half-written
 *       state is never committed. Reads use {@code readOnly = true} which lets
 *       the provider optimize (no dirty-checking/flush).</li>
 *   <li><b>Never store plaintext</b>: passwords are hashed with BCrypt before
 *       persistence; verification compares against the stored hash.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Sits between controllers and the repository; collaborates with
 * {@code JwtService} (tokens), {@code PasswordEncoder} (hashing), and
 * {@code AuthenticationManager} (credential checks).
 *
 * @author SmartShop Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * {@inheritDoc}
     *
     * <p>Rejects duplicate emails up front, hashes the password, persists the
     * new CUSTOMER, and returns a token so the client is logged in immediately.
     */
    @Override
    @Transactional // write operation: must be atomic + roll back on failure.
    public AuthResponse register(RegisterRequest request) {
        // Fail fast on duplicates to give a clean 409 instead of a DB constraint
        // violation surfacing later.
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = User.builder()
                .email(request.email())
                // BCrypt: salts + stretches the password before storage.
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                // New self-service signups are CUSTOMERs by default.
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        User saved = userRepository.save(user);
        log.info("Registered new user id={} email={}", saved.getId(), saved.getEmail());

        String token = jwtService.generateToken(saved);
        return AuthResponse.bearer(token, jwtService.getExpirationMs(), userMapper.toDto(saved));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates credential verification to the {@link AuthenticationManager}
     * (which uses our UserDetailsService + BCrypt), then mints a JWT.
     */
    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Throws BadCredentialsException (mapped to 401) if email/password wrong.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(request.email()));

        String token = jwtService.generateToken(user);
        log.info("User logged in: {}", user.getEmail());
        return AuthResponse.bearer(token, jwtService.getExpirationMs(), userMapper.toDto(user));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public UserDto getById(Long id) {
        return userMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(id))));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public UserDto getByEmail(String email) {
        return userMapper.toDto(userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email)));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Only the display name is mutable here; email/role changes are
     * intentionally out of scope for this endpoint.
     */
    @Override
    @Transactional
    public UserDto update(Long id, RegisterRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(id)));
        user.setFullName(request.fullName());
        // Dirty-checking within the transaction flushes the change on commit;
        // an explicit save() is optional but kept for clarity.
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }
}
