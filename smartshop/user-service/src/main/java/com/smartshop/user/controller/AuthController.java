package com.smartshop.user.controller;

import com.smartshop.user.dto.AuthResponse;
import com.smartshop.user.dto.LoginRequest;
import com.smartshop.user.dto.RegisterRequest;
import com.smartshop.user.dto.UserDto;
import com.smartshop.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthController - Authentication API endpoints.
 *
 * <h2>Purpose</h2>
 * Exposes register/login/me endpoints for stateless identity workflows used by external clients.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Controller thinness: delegates business logic to service layer.</li>
 *   <li>OpenAPI annotations: document request/response behavior automatically.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Gateway forwards auth requests here; returned tokens authenticate future calls.
 *
 * @see com.smartshop.user.service.UserService
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * Registers a new account and returns JWT.
     *
     * @param request registration payload
     * @return token + user details
     */
    @Operation(summary = "Register new user")
    @ApiResponse(responseCode = "200", description = "Registration successful")
    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody final RegisterRequest request) {
        return userService.register(request);
    }

    /**
     * Authenticates existing user credentials.
     *
     * @param request login payload
     * @return token + user details
     */
    @Operation(summary = "Login user")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody final LoginRequest request) {
        return userService.login(request);
    }

    /**
     * Returns currently authenticated user profile.
     *
     * @param authentication spring security principal
     * @return authenticated user dto
     */
    @Operation(summary = "Get current authenticated user")
    @ApiResponse(responseCode = "200", description = "Current user returned")
    @GetMapping("/me")
    public UserDto me(@Parameter(hidden = true) final Authentication authentication) {
        return userService.me(authentication.getName());
    }
}
