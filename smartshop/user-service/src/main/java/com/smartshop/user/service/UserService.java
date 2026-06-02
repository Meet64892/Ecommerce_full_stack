package com.smartshop.user.service;

import com.smartshop.user.dto.AuthResponse;
import com.smartshop.user.dto.LoginRequest;
import com.smartshop.user.dto.RegisterRequest;
import com.smartshop.user.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * UserService - Application service contract for users and authentication.
 *
 * <h2>Purpose</h2>
 * A service interface separates controller contracts from business logic implementation. This makes tests and future
 * alternative implementations easier while keeping transactions in the service layer.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Transaction boundary: Service methods define the unit of work, not controllers.</li>
 *   <li>Pagination: Pageable lets clients request slices of large user lists safely.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Controllers call this interface and UserServiceImpl coordinates repositories, security, and mappers.
 *
 * @see UserServiceImpl
 * @author SmartShop Team
 */
public interface UserService {
    /**
     * Registers a new account and returns a login token.
     *
     * @param request validated registration payload
     * @return authentication response containing JWT and public user data
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates an existing account and returns a new token.
     *
     * @param request login credentials
     * @return authentication response containing JWT and public user data
     */
    AuthResponse login(LoginRequest request);

    /**
     * Loads a user by id.
     *
     * @param id user UUID
     * @return public user DTO
     */
    UserDto getUser(UUID id);

    /**
     * Updates simple profile fields.
     *
     * @param id user UUID
     * @param request profile values to apply
     * @return updated public user DTO
     */
    UserDto updateUser(UUID id, RegisterRequest request);

    /**
     * Returns a paginated list of users.
     *
     * @param pageable page number, size, and sorting requested by the client
     * @return page of user DTOs with metadata
     */
    Page<UserDto> listUsers(Pageable pageable);

    /**
     * Returns the currently authenticated user's profile.
     *
     * @return public user DTO for the SecurityContext principal
     */
    UserDto currentUser();
}
