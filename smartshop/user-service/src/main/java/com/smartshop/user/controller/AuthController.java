package com.smartshop.user.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.user.dto.AuthResponse;
import com.smartshop.user.dto.LoginRequest;
import com.smartshop.user.dto.RegisterRequest;
import com.smartshop.user.dto.UserDto;
import com.smartshop.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthController - Exposes registration, login, and current-user endpoints.
 *
 * <h2>Purpose</h2>
 * Authentication endpoints form the public entry point to identity management. They translate HTTP requests into
 * service-layer commands and return a consistent ApiResponse wrapper.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>@Valid: Triggers Bean Validation before business logic runs.</li>
 *   <li>OpenAPI annotations: Document operations so Swagger UI can teach and test the API.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Clients call these endpoints through the gateway; UserService handles password hashing, authentication, and JWTs.
 *
 * @see UserService
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Registration and JWT login endpoints")
public class AuthController {
    private final UserService userService;

    /**
     * Creates the controller with its service dependency.
     *
     * @param userService user application service
     */
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user account.
     *
     * @param request validated registration payload
     * @return HTTP 201 with JWT and user data
     */
    @Operation(summary = "Register a user", description = "Creates a customer account and returns a JWT.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userService.register(request), "User registered"));
    }

    /**
     * Authenticates a user with email and password.
     *
     * @param request validated login payload
     * @return HTTP 200 with JWT and user data
     */
    @Operation(summary = "Login", description = "Authenticates credentials and returns a bearer token.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful")
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(userService.login(request), "Login successful");
    }

    /**
     * Returns the profile for the current authenticated principal.
     *
     * @return current user DTO wrapped in ApiResponse
     */
    @Operation(summary = "Current user", description = "Returns the authenticated user's profile.")
    @GetMapping("/me")
    public ApiResponse<UserDto> me() {
        return ApiResponse.success(userService.currentUser(), "Current user loaded");
    }
}
