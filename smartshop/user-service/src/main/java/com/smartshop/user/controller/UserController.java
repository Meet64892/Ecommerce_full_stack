package com.smartshop.user.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.user.dto.RegisterRequest;
import com.smartshop.user.dto.UserDto;
import com.smartshop.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * UserController - Exposes user profile CRUD-style endpoints.
 *
 * <h2>Purpose</h2>
 * Profile operations are separated from authentication operations so endpoint responsibilities stay clear. The
 * controller delegates business rules to UserService and keeps HTTP mapping concerns at the edge.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Pageable: Spring binds page, size, and sort query parameters automatically.</li>
 *   <li>DTO input/output: Controllers never expose JPA entities directly.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * The gateway routes `/users/**` here after JWT validation, then UserService loads data from PostgreSQL.
 *
 * @see UserService
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User profile management")
public class UserController {
    private final UserService userService;

    /**
     * Creates the controller with constructor injection for testability.
     *
     * @param userService user application service
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Loads a user by id.
     *
     * @param id user UUID from the path
     * @return public user DTO
     */
    @Operation(summary = "Get user", description = "Loads one user by UUID.")
    @GetMapping("/{id}")
    public ApiResponse<UserDto> getUser(@Parameter(description = "User id") @PathVariable UUID id) {
        return ApiResponse.success(userService.getUser(id), "User loaded");
    }

    /**
     * Updates user profile fields.
     *
     * @param id user UUID from the path
     * @param request validated profile payload
     * @return updated public user DTO
     */
    @Operation(summary = "Update user", description = "Updates basic profile fields.")
    @PutMapping("/{id}")
    public ApiResponse<UserDto> updateUser(@PathVariable UUID id, @Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(userService.updateUser(id, request), "User updated");
    }

    /**
     * Lists users using Spring Data pagination.
     *
     * @param pageable page request generated from query parameters
     * @return page of users and pagination metadata
     */
    @Operation(summary = "List users", description = "Returns a paginated list of users.")
    @GetMapping
    public ApiResponse<Page<UserDto>> listUsers(Pageable pageable) {
        return ApiResponse.success(userService.listUsers(pageable), "Users loaded");
    }
}
