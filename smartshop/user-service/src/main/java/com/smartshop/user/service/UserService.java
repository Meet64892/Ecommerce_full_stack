package com.smartshop.user.service;

import com.smartshop.user.dto.*;
import java.util.List;

/**
 * UserService - User domain application service contract.
 *
 * <h2>Purpose</h2>
 * Defines use-case-oriented operations for auth and profile management.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Service abstraction: controller remains thin and technology agnostic.</li>
 *   <li>Transactional boundaries: write operations execute atomically.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Controllers depend on this interface; implementation coordinates repository, mapping, and JWT.
 *
 * @see UserServiceImpl
 * @author SmartShop Team
 */
public interface UserService {

    /**
     * Registers a new user account.
     *
     * @param request validated registration payload
     * @return auth response with JWT and user summary
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates user and returns signed token.
     *
     * @param request login credentials
     * @return auth response with token and profile
     */
    AuthResponse login(LoginRequest request);

    /**
     * Retrieves one user by id.
     *
     * @param id user id
     * @return user projection
     */
    UserDto getById(Long id);

    /**
     * Lists all users.
     *
     * @return user DTO list
     */
    List<UserDto> getAll();

    /**
     * Updates mutable profile fields.
     *
     * @param id target user id
     * @param request profile payload
     * @return updated user DTO
     */
    UserDto update(Long id, RegisterRequest request);

    /**
     * Resolves caller profile by email from token subject.
     *
     * @param email authenticated principal email
     * @return user dto
     */
    UserDto me(String email);
}
