package com.smartshop.user.service;

import com.smartshop.user.dto.AuthResponse;
import com.smartshop.user.dto.LoginRequest;
import com.smartshop.user.dto.RegisterRequest;
import com.smartshop.user.dto.UserDto;
import com.smartshop.user.entity.Role;
import com.smartshop.user.entity.User;
import com.smartshop.user.exception.EmailAlreadyExistsException;
import com.smartshop.user.event.UserRegisteredEvent;
import com.smartshop.user.exception.UserNotFoundException;
import com.smartshop.user.kafka.UserEventProducer;
import com.smartshop.user.mapper.UserMapper;
import com.smartshop.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * UserServiceImpl - Implements authentication and user profile use cases.
 *
 * <h2>Purpose</h2>
 * The service layer is the transaction boundary where business rules, security adapters, repositories, and mappers
 * meet. @Transactional provides ACID behavior for database work and rolls back runtime exceptions by default.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Propagation: Default REQUIRED joins an existing transaction or starts a new one for the method.</li>
 *   <li>Optional: Repository lookups are handled explicitly so null values do not cause hidden failures.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Controllers call this class; it persists users, hashes passwords, authenticates credentials, and returns DTOs.
 *
 * @see UserService
 * @author SmartShop Team
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final UserEventProducer userEventProducer;

    /**
     * Constructor injection keeps all dependencies explicit and immutable.
     *
     * @param userRepository repository for user persistence
     * @param passwordEncoder BCrypt password encoder
     * @param authenticationManager Spring Security authentication manager
     * @param jwtService token generator and validator
     * @param userMapper entity-to-DTO mapper
     * @param userEventProducer Kafka publisher for registration events
     */
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager, JwtService jwtService, UserMapper userMapper,
                           UserEventProducer userEventProducer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.userEventProducer = userEventProducer;
    }

    /**
     * Registers a user after checking uniqueness and hashing the password.
     *
     * @param request validated registration payload
     * @return token and public user data for immediate login
     * @throws EmailAlreadyExistsException when the email is already registered
     */
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException(normalizedEmail);
        }
        // We hash the password before constructing the entity so raw credentials never enter persistence objects.
        User user = new User(normalizedEmail, passwordEncoder.encode(request.password()), request.firstName(), request.lastName(), Role.CUSTOMER);
        User saved = userRepository.save(user);
        userEventProducer.publishRegistered(new UserRegisteredEvent(saved.getId(), saved.getEmail(), saved.getFirstName()));
        return authResponse(saved);
    }

    /**
     * Authenticates credentials through Spring Security and returns a new JWT.
     *
     * @param request login credentials
     * @return token and public user data
     * @throws UserNotFoundException when the authenticated email cannot be loaded afterward
     */
    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(normalizedEmail, request.password()));
        User user = userRepository.findByEmail(normalizedEmail).orElseThrow(() -> new UserNotFoundException(normalizedEmail));
        return authResponse(user);
    }

    /**
     * Retrieves one user by id.
     *
     * @param id user UUID
     * @return public user data
     * @throws UserNotFoundException when no user exists for the id
     */
    @Override
    @Transactional(readOnly = true)
    public UserDto getUser(UUID id) {
        return userRepository.findById(id).map(userMapper::toDto).orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Updates profile names; email/password changes would normally use dedicated flows.
     *
     * @param id user UUID
     * @param request profile values to apply
     * @return updated public user data
     * @throws UserNotFoundException when no user exists for the id
     */
    @Override
    @Transactional
    public UserDto updateUser(UUID id, RegisterRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        return userMapper.toDto(user);
    }

    /**
     * Lists users with Spring Data pagination.
     *
     * @param pageable page request; Page includes total counts while Slice omits count queries for cheaper scrolling
     * @return page of public user data
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    /**
     * Loads the authenticated user's profile from the SecurityContext.
     *
     * @return current user profile
     * @throws UserNotFoundException when the principal no longer maps to a database row
     */
    @Override
    @Transactional(readOnly = true)
    public UserDto currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).map(userMapper::toDto).orElseThrow(() -> new UserNotFoundException(email));
    }

    /**
     * Builds a consistent token response for registration and login.
     *
     * @param user authenticated user entity
     * @return auth response with bearer token type and expiry
     */
    private AuthResponse authResponse(User user) {
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, "Bearer", jwtService.expirationSeconds(), userMapper.toDto(user));
    }
}
