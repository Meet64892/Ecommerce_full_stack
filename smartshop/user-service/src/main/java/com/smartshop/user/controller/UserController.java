package com.smartshop.user.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.user.dto.UserDto;
import com.smartshop.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * UserController - REST Endpoints for User Profile Management
 *
 * <h2>Purpose</h2>
 * Handles authenticated requests for reading and updating user profiles.
 * All endpoints require authentication (JWT token) via SecurityConfig rules.
 *
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "User profile management endpoints")
@SecurityRequirement(name = "bearerAuth")  // Swagger: all endpoints require Bearer token
public class UserController {

    private final UserService userService;

    /**
     * Returns all users with pagination.
     * @PreAuthorize restricts this to ADMIN role only.
     * @PageableDefault sets default pagination: page=0, size=20, sort=id
     *
     * @param pageable Spring auto-populates from query params: ?page=0&size=20&sort=email,asc
     * @return paginated list of users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get all users (Admin only)",
        description = "Returns a paginated list of all users. " +
                      "Supports sorting and filtering via Pageable: ?page=0&size=20&sort=email,asc"
    )
    public ResponseEntity<ApiResponse<Page<UserDto>>> getAllUsers(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        log.debug("GET /users?page={}&size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<UserDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    /**
     * Returns a specific user's profile by ID.
     *
     * @param id the user ID from the path variable
     * @return the user's profile
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(
            @Parameter(description = "The user's unique ID") @PathVariable Long id) {
        log.debug("GET /users/{}", id);
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * Updates a user's profile.
     * Only the user themselves or an ADMIN can update a profile.
     * @PreAuthorize uses SpEL (Spring Expression Language) to check both conditions.
     *
     * @param id      the ID of the user to update
     * @param request the updated user data
     * @return the updated user profile
     */
    @PutMapping("/{id}")
    @PreAuthorize("@userSecurityService.canModifyUser(authentication, #id)")
    @Operation(summary = "Update user profile")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(
            @PathVariable Long id,
            @RequestBody UserDto request) {
        log.info("PUT /users/{}", id);
        UserDto updated = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "User updated successfully"));
    }
}
