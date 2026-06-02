package com.smartshop.user.controller;

import com.smartshop.user.dto.RegisterRequest;
import com.smartshop.user.dto.UserDto;
import com.smartshop.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * UserController - User management endpoints.
 *
 * <h2>Purpose</h2>
 * Provides CRUD-like profile operations separate from authentication endpoints for cleaner API
 * boundaries and easier authorization policy control.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>REST resource design: users are addressed by stable identifiers.</li>
 *   <li>Validation pipeline: update payload is validated before persistence.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Called through gateway; delegates all domain logic to UserService.
 *
 * @see UserService
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Fetches a user by id.
     *
     * @param id user identifier
     * @return user dto
     */
    @Operation(summary = "Get user by ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @GetMapping("/{id}")
    public UserDto getById(@Parameter(description = "User ID") @PathVariable final Long id) {
        return userService.getById(id);
    }

    /**
     * Updates user profile data.
     *
     * @param id user id
     * @param request update payload
     * @return updated user dto
     */
    @Operation(summary = "Update user")
    @ApiResponse(responseCode = "200", description = "User updated")
    @PutMapping("/{id}")
    public UserDto update(@PathVariable final Long id, @Valid @RequestBody final RegisterRequest request) {
        return userService.update(id, request);
    }

    /**
     * Lists users.
     *
     * @return user list
     */
    @Operation(summary = "List users")
    @ApiResponse(responseCode = "200", description = "Users returned")
    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }
}
