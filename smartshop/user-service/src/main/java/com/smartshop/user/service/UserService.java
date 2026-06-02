package com.smartshop.user.service;

import com.smartshop.user.dto.AuthResponse;
import com.smartshop.user.dto.LoginRequest;
import com.smartshop.user.dto.RegisterRequest;
import com.smartshop.user.dto.UserDto;

import java.util.List;

/**
 * UserService - Business operations for authentication and user management.
 *
 * <h2>Purpose</h2>
 * Defines the contract the controllers depend on. Programming to an interface
 * (rather than the concrete class) decouples the web layer from the
 * implementation and makes it trivial to mock in tests.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Interface-driven design: enables alternative implementations and clean
 *       unit tests.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Implemented by {@code UserServiceImpl}; consumed by {@code AuthController} and
 * {@code UserController}.
 *
 * @author SmartShop Team
 */
public interface UserService {

    /**
     * Registers a new account and returns an authenticated session.
     *
     * @param request validated registration data
     * @return an {@link AuthResponse} containing a fresh JWT and the user view
     * @throws com.smartshop.user.exception.EmailAlreadyExistsException if taken
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates credentials and issues a JWT.
     *
     * @param request validated login data
     * @return an {@link AuthResponse} with a JWT and the user view
     */
    AuthResponse login(LoginRequest request);

    /**
     * Fetches a single user by id.
     *
     * @param id the user id
     * @return the user view
     * @throws com.smartshop.user.exception.UserNotFoundException if absent
     */
    UserDto getById(Long id);

    /**
     * Fetches a user by email (used by {@code /auth/me}).
     *
     * @param email the user's email
     * @return the user view
     * @throws com.smartshop.user.exception.UserNotFoundException if absent
     */
    UserDto getByEmail(String email);

    /**
     * Lists all users (admin operation).
     *
     * @return all users as DTOs
     */
    List<UserDto> getAll();

    /**
     * Updates a user's profile fields.
     *
     * @param id      the user id
     * @param request the new profile values (full name validated)
     * @return the updated user view
     * @throws com.smartshop.user.exception.UserNotFoundException if absent
     */
    UserDto update(Long id, RegisterRequest request);
}
