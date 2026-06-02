package com.smartshop.user.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.user.dto.AuthResponse;
import com.smartshop.user.dto.LoginRequest;
import com.smartshop.user.dto.RegisterRequest;
import com.smartshop.user.dto.UserDto;
import com.smartshop.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthController - Public authentication endpoints.
 *
 * <h2>Purpose</h2>
 * Exposes registration, login, and "who am I" (/me). The first two are public;
 * /me requires a valid token and demonstrates reading the authenticated principal.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@code @Valid} triggers Bean Validation on the request body; failures are
 *       turned into 400s by the GlobalExceptionHandler.</li>
 *   <li>Responses are wrapped in the shared {@link ApiResponse} envelope.</li>
 *   <li>{@code @AuthenticationPrincipal} injects the current Spring Security user
 *       established by the JWT filter.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Reached via the gateway at {@code /api/users/auth/**} (prefix stripped to
 * {@code /auth/**}). Delegates all logic to {@link UserService}.
 *
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login, and current-user endpoints")
public class AuthController {

    private final UserService userService;

    /**
     * Registers a new account.
     *
     * @param request validated registration payload
     * @return 201 Created with a JWT + user view
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a CUSTOMER account and returns a JWT.")
    @ApiResponses(@io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", description = "User registered"))
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse result = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(result, "Registration successful"));
    }

    /**
     * Authenticates and issues a token.
     *
     * @param request validated login payload
     * @return 200 OK with a JWT + user view
     */
    @PostMapping("/login")
    @Operation(summary = "Authenticate", description = "Verifies credentials and returns a JWT.")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse result = userService.login(request);
        return ResponseEntity.ok(ApiResponse.of(result, "Login successful"));
    }

    /**
     * Returns the currently authenticated user's profile.
     *
     * @param principal the security principal injected from the JWT
     * @return 200 OK with the user view
     */
    @GetMapping("/me")
    @Operation(summary = "Current user", description = "Returns the profile of the authenticated caller.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserDto>> me(@AuthenticationPrincipal UserDetails principal) {
        UserDto dto = userService.getByEmail(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }
}
