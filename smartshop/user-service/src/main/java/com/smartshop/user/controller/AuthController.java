package com.smartshop.user.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.user.dto.AuthResponse;
import com.smartshop.user.dto.LoginRequest;
import com.smartshop.user.dto.RegisterRequest;
import com.smartshop.user.dto.UserDto;
import com.smartshop.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController - REST Endpoints for Authentication and Registration
 *
 * <h2>Purpose</h2>
 * Handles all unauthenticated requests for user registration and login.
 * Returns JWT tokens on success.
 *
 * <h2>How it fits in the system</h2>
 * Client → Gateway (/api/auth/**) → user-service (/auth/**) → AuthController
 * The gateway strips the /api prefix via StripPrefix filter.
 *
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User registration and login endpoints")
public class AuthController {

    // Constructor-injected (via @RequiredArgsConstructor) — preferred over @Autowired
    private final UserService userService;

    /**
     * Registers a new user and returns a JWT token.
     * @Valid triggers Bean Validation on the RegisterRequest body.
     * If validation fails, MethodArgumentNotValidException is thrown
     * and caught by GlobalExceptionHandler before this method is called.
     *
     * @param request the validated registration details
     * @return 201 Created with JWT token and user info
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account and returns a JWT access token. " +
                      "The token can be used immediately for authenticated requests."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation failed — see details in response body"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "Email or username already exists"
        )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /auth/register for email: {}", request.email());
        AuthResponse authResponse = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(authResponse, "User registered successfully"));
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request the login credentials
     * @return 200 OK with JWT token and user info
     */
    @PostMapping("/login")
    @Operation(
        summary = "Login with email/username and password",
        description = "Authenticates the user and returns a JWT access token valid for 24 hours."
    )
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /auth/login for: {}", request.emailOrUsername());
        AuthResponse authResponse = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(authResponse, "Login successful"));
    }

    /**
     * Returns the profile of the currently authenticated user.
     * The userId is extracted from the X-User-Id header injected by the API gateway.
     *
     * @param userId the authenticated user's ID (injected by gateway via header)
     * @return the current user's profile
     */
    @GetMapping("/me")
    @Operation(
        summary = "Get current user profile",
        description = "Returns the profile of the authenticated user. Requires a valid JWT token."
    )
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(
            @RequestHeader("X-User-Id") Long userId) {
        log.debug("GET /auth/me for userId: {}", userId);
        UserDto user = userService.getCurrentUser(userId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}
