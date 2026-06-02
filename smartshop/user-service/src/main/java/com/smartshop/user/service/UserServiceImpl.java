package com.smartshop.user.service;

import com.smartshop.user.dto.*;
import com.smartshop.user.entity.Role;
import com.smartshop.user.entity.User;
import com.smartshop.user.event.UserRegisteredEvent;
import com.smartshop.user.exception.EmailAlreadyExistsException;
import com.smartshop.user.exception.UserNotFoundException;
import com.smartshop.user.mapper.UserMapper;
import com.smartshop.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * UserServiceImpl - User use-case orchestration implementation.
 *
 * <h2>Purpose</h2>
 * Coordinates validation, persistence, password hashing, JWT creation, and domain event
 * publication. Constructor injection is used because it makes dependencies explicit, immutable,
 * and easy to mock in tests.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>@Transactional: groups write operations into ACID units with rollback on failure.</li>
 *   <li>Domain events: registration emits user.registered for async notifications.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Called by controllers and relies on repository, mapper, security, and Kafka collaborators.
 *
 * @see UserService
 * @author SmartShop Team
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Registers a new user with hashed password and default CUSTOMER role.
     *
     * @param request validated registration payload
     * @return auth response with JWT token and user dto
     * @throws EmailAlreadyExistsException when email already exists
     */
    @Override
    @Transactional
    public AuthResponse register(final RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already in use: " + request.email());
        }

        final User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .role(Role.CUSTOMER)
                .build();

        final User saved = userRepository.save(user);
        kafkaTemplate.send("user.registered", new UserRegisteredEvent(saved.getId(), saved.getEmail(), saved.getFirstName()));

        final String token = jwtService.generateToken(saved.getEmail(), saved.getRole());
        return new AuthResponse(token, userMapper.toDto(saved));
    }

    /**
     * Authenticates credentials and returns fresh JWT.
     *
     * @param request login payload
     * @return auth response
     * @throws UserNotFoundException when principal cannot be found after authentication
     */
    @Override
    public AuthResponse login(final LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        final User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("User not found for email: " + request.email()));

        final String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token, userMapper.toDto(user));
    }

    /**
     * Retrieves user by id.
     *
     * @param id user id
     * @return dto projection
     * @throws UserNotFoundException when id does not exist
     */
    @Override
    public UserDto getById(final Long id) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + id));
        return userMapper.toDto(user);
    }

    /**
     * Returns all users.
     *
     * @return list of user dto objects
     */
    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    /**
     * Updates profile name and optionally password.
     *
     * @param id user id
     * @param request update payload
     * @return updated dto
     * @throws UserNotFoundException when user is missing
     */
    @Override
    @Transactional
    public UserDto update(final Long id, final RegisterRequest request) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + id));

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        // Re-hash only if a new password was provided to maintain secure-at-rest credentials.
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        final User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    /**
     * Resolves current user from principal email.
     *
     * @param email authenticated subject
     * @return dto projection
     */
    @Override
    public UserDto me(final String email) {
        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found for email: " + email));
        return userMapper.toDto(user);
    }
}
