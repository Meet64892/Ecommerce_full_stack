package com.smartshop.user.service;

import com.smartshop.user.dto.*;
import com.smartshop.user.entity.Role;
import com.smartshop.user.entity.User;
import com.smartshop.user.exception.EmailAlreadyExistsException;
import com.smartshop.user.exception.UserNotFoundException;
import com.smartshop.user.mapper.UserMapper;
import com.smartshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserServiceImpl - Business Logic Implementation for User Operations
 *
 * <h2>Purpose</h2>
 * Contains all user-related business rules: password hashing, uniqueness validation,
 * authentication logic, and user profile management.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>@Transactional: Wraps the method in a database transaction.
 *       ACID guarantees:
 *         A (Atomicity): All DB changes in the method happen or none do
 *         C (Consistency): DB goes from one valid state to another
 *         I (Isolation): Concurrent transactions don't interfere (configurable)
 *         D (Durability): Committed changes survive server restarts
 *       Rollback behavior: @Transactional auto-rolls back on RuntimeException or Error.
 *       For checked exceptions, you must specify: @Transactional(rollbackFor = Exception.class).</li>
 *   <li>@Transactional(readOnly = true): Optimizes read-only queries:
 *       - Hibernate skips dirty checking (comparing entity state before/after method)
 *       - Some databases route reads to a read replica
 *       - Prevents accidental writes in read methods (good defensive programming)</li>
 *   <li>Constructor Injection: @RequiredArgsConstructor generates a constructor with
 *       all final fields. Spring calls this constructor with the beans injected.
 *       WHY prefer constructor injection over @Autowired field injection?
 *         - Dependencies are explicit (listed in the constructor)
 *         - Objects are immutable after construction
 *         - Easier to test: just call new UserServiceImpl(mockRepo, mockEncoder, ...)
 *         - Spring itself recommends constructor injection since Spring 4.3</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    // final fields: set once in constructor, never changed (immutability)
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    /**
     * {@inheritDoc}
     *
     * Validates uniqueness of email and username, BCrypt-hashes the password,
     * creates the User entity, persists it, and returns a JWT token.
     * The entire method is transactional: if saving fails, no user record is created.
     */
    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.email());

        // Check email uniqueness BEFORE constructing the entity.
        // The DB has a UNIQUE constraint too, but checking here gives a clear
        // business exception instead of a cryptic DataIntegrityViolationException.
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        // Check username uniqueness
        if (userRepository.existsByUsername(request.username())) {
            throw new EmailAlreadyExistsException("Username '" + request.username() + "' is already taken");
        }

        // Build the User entity from the DTO
        User user = userMapper.toEntity(request);

        // BCrypt the password — the plain-text password MUST NOT be stored
        // passwordEncoder.encode() generates a random salt and hashes:
        // BCrypt(password + salt) → "$2a$10$[salt+hash]"
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        // Assign default role — all self-registrations get CUSTOMER role.
        // ADMIN role can only be granted by another admin via a separate endpoint.
        user.setRole(Role.CUSTOMER);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id={}", savedUser.getId());

        // Generate JWT token immediately so the user is "logged in" after registration
        String token = jwtService.generateToken(savedUser);

        return AuthResponse.of(
                token,
                jwtService.getTokenExpirationMs(token),
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    /**
     * {@inheritDoc}
     *
     * Verifies credentials and returns a JWT token on success.
     * readOnly=false (default) because Spring Security's UserDetails loading
     * might update last-login timestamp in some implementations.
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for: {}", request.emailOrUsername());

        // Find user by email OR username
        User user = userRepository.findByEmailOrUsername(
                        request.emailOrUsername(), request.emailOrUsername())
                .orElseThrow(() -> {
                    // NOTE: We use a generic "invalid credentials" message.
                    // NEVER reveal WHETHER the email exists — that would allow
                    // user enumeration attacks (enumerating valid email addresses).
                    log.warn("Login failed: user not found for identifier: {}", request.emailOrUsername());
                    return new BadCredentialsException("Invalid email/username or password");
                });

        // Check if account is active
        if (!user.isEnabled()) {
            log.warn("Login attempt on disabled account: {}", user.getEmail());
            throw new BadCredentialsException("Account is disabled. Please contact support.");
        }

        // BCrypt comparison: passwordEncoder.matches(plaintext, storedHash)
        // This is NOT decryption — it re-hashes the plaintext with the stored salt
        // and compares the result. The original password is NEVER recoverable from the hash.
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Login failed: incorrect password for user id={}", user.getId());
            throw new BadCredentialsException("Invalid email/username or password");
        }

        log.info("User id={} logged in successfully", user.getId());
        String token = jwtService.generateToken(user);

        return AuthResponse.of(
                token,
                jwtService.getTokenExpirationMs(token),
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

    /**
     * {@inheritDoc}
     * readOnly=true: this is a pure read operation — no DB writes needed.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDto getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                // orElseThrow with lambda: the exception is only constructed if needed
                // (compared to orElseThrow(new Exception()) which always constructs it)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return userMapper.toDto(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toDto(user);
    }

    /**
     * {@inheritDoc}
     *
     * Returns a page of users. Pagination is CRITICAL for large datasets:
     * - Without pagination, GET /users on a platform with 1M users would load
     *   all 1M records into memory (OutOfMemoryError)
     * - With pagination, only 20 records are loaded per request
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        // Page.map() transforms each element without loading a new Page object
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDto updateUser(Long id, UserDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Only update fields that are non-null in the request
        // This is a PATCH-style update (update only provided fields)
        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }

        // Note: email and username changes require uniqueness re-checking
        // and potentially token invalidation — omitted here for brevity

        // @Transactional: the save() is not strictly necessary — Hibernate's
        // dirty checking detects that `user` has changed and auto-flushes at
        // transaction commit. But explicit save() makes the intent clear.
        User saved = userRepository.save(user);
        log.info("Updated user id={}", id);
        return userMapper.toDto(saved);
    }
}
