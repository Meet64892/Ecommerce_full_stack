package com.smartshop.user.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.user.dto.RegisterRequest;
import com.smartshop.user.dto.UserDto;
import com.smartshop.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * UserController - Protected user-management endpoints.
 *
 * <h2>Purpose</h2>
 * CRUD-style reads/updates over users. These require authentication, and listing
 * all users is restricted to ADMINs to demonstrate role-based method security.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@code @PreAuthorize("hasRole('ADMIN')")}: method-level authorization
 *       (enabled by {@code @EnableMethodSecurity}); denies non-admins with 403.</li>
 *   <li>{@code @Parameter} documents path variables in Swagger.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Reached via the gateway at {@code /api/users/users/**} (after StripPrefix).
 *
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    /**
     * Fetches a user by id.
     *
     * @param id the user id
     * @return 200 OK with the user view
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by id")
    public ResponseEntity<ApiResponse<UserDto>> getById(
            @Parameter(description = "User id") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getById(id)));
    }

    /**
     * Lists all users. ADMIN only.
     *
     * @return 200 OK with all users
     */
    @GetMapping
    @Operation(summary = "List all users (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getAll()));
    }

    /**
     * Updates a user's profile.
     *
     * @param id      the user id
     * @param request validated profile payload
     * @return 200 OK with the updated user view
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a user's profile")
    public ResponseEntity<ApiResponse<UserDto>> update(
            @Parameter(description = "User id") @PathVariable Long id,
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.of(userService.update(id, request), "User updated"));
    }
}
