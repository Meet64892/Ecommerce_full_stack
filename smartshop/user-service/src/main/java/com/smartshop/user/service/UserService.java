package com.smartshop.user.service;

import com.smartshop.user.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * UserService - Service Layer Interface for User Operations
 *
 * <h2>Purpose</h2>
 * Defines the contract for all user-related business operations.
 * Programming to an interface (not the implementation) enables:
 *   1. Easy mocking in unit tests: @MockBean UserService mockService
 *   2. Swapping implementations without changing controllers
 *   3. Multiple implementations (e.g., UserServiceImpl vs CachedUserServiceImpl)
 *
 * The controller depends on UserService (interface), not UserServiceImpl (class).
 * This is the Dependency Inversion Principle (the 'D' in SOLID).
 *
 * @author SmartShop Team
 */
public interface UserService {

    /**
     * Registers a new user with the given details.
     * Validates uniqueness of email and username, hashes the password, and persists the user.
     *
     * @param request the validated registration request
     * @return AuthResponse containing the JWT token and user info
     * @throws com.smartshop.user.exception.EmailAlreadyExistsException if email is taken
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates a user and returns a JWT token.
     * Verifies the password against the stored BCrypt hash.
     *
     * @param request the login credentials
     * @return AuthResponse containing the JWT token and user info
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are wrong
     */
    AuthResponse login(LoginRequest request);

    /**
     * Returns the profile of the currently authenticated user.
     *
     * @param userId the ID of the authenticated user (from JWT, set by gateway)
     * @return the user's profile as a DTO
     * @throws com.smartshop.user.exception.UserNotFoundException if user doesn't exist
     */
    UserDto getCurrentUser(Long userId);

    /**
     * Returns a specific user's profile by ID.
     *
     * @param id the user ID to look up
     * @return the user's profile as a DTO
     * @throws com.smartshop.user.exception.UserNotFoundException if not found
     */
    UserDto getUserById(Long id);

    /**
     * Returns a paginated list of all users.
     * Pagination prevents loading thousands of users into memory at once.
     *
     * @param pageable pagination parameters (page number, page size, sort)
     * @return a Page containing the current page of users
     */
    Page<UserDto> getAllUsers(Pageable pageable);

    /**
     * Updates a user's profile information.
     * Only the authenticated user can update their own profile (or an admin can update any).
     *
     * @param id      the ID of the user to update
     * @param request the updated user data
     * @return the updated user as a DTO
     */
    UserDto updateUser(Long id, UserDto request);
}
