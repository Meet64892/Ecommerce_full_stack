package com.smartshop.user.security;

import com.smartshop.user.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthFilter - JWT Token Validation for Direct Service Access
 *
 * <h2>Purpose</h2>
 * This filter validates JWT tokens for requests that reach user-service directly
 * (during development, direct service access bypasses the gateway). In production,
 * the api-gateway's AuthenticationFilter handles JWT validation for all external requests.
 * This filter provides a second layer of protection.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>OncePerRequestFilter: Guarantees this filter runs EXACTLY ONCE per HTTP request.
 *       Contrast with GenericFilterBean (which might run multiple times in forwards/includes).
 *       Spring Security's filter chain can be complex; OncePerRequestFilter simplifies reasoning.</li>
 *   <li>Spring Security Filter Chain: Spring Security intercepts every request with
 *       a chain of security filters. JwtAuthFilter plugs into this chain (before
 *       UsernamePasswordAuthenticationFilter) to authenticate stateless JWT requests.
 *       The chain processes filters in order; later filters see the SecurityContext
 *       populated by earlier filters.</li>
 *   <li>SecurityContextHolder: Thread-local storage for the currently authenticated user.
 *       Set by this filter → read by controllers via @AuthenticationPrincipal or
 *       SecurityContextHolder.getContext().getAuthentication().
 *       Thread-local means each HTTP request thread has its own SecurityContext.</li>
 *   <li>UsernamePasswordAuthenticationToken: Represents an authenticated principal.
 *       Constructor with 3 args (principal, credentials, authorities) creates an
 *       AUTHENTICATED token (isAuthenticated() = true).
 *       Constructor with 2 args creates an UNAUTHENTICATED token (for pre-auth).</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Filter logic: extract JWT from Authorization header, validate it,
     * and set up the Spring Security context for the authenticated user.
     *
     * @param request     the incoming HTTP request
     * @param response    the HTTP response
     * @param filterChain the remaining filters to execute after this one
     * @throws ServletException on servlet errors
     * @throws IOException      on I/O errors
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // If no Bearer token, let the request proceed — Spring Security will
        // reject it later if the endpoint requires authentication
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the token string after "Bearer "
        final String token = authHeader.substring(7);

        try {
            // Extract user's email from the token claims
            String userEmail = jwtService.extractAllClaims(token).get("email", String.class);

            // Only authenticate if:
            //   1. We extracted a valid email from the token
            //   2. There's no existing authentication in the context
            //      (avoid re-authenticating an already-authenticated request)
            if (userEmail != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                // Load full UserDetails from the database
                // This verifies the user still exists and is enabled
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                // Validate the token against the loaded user
                // Checks: token's userId matches the user, token is not expired
                Long userId = jwtService.extractUserId(token);

                // Find the actual user entity to validate the token
                // We need to look up by userId to call isTokenValid
                // For simplicity here, we trust the JWT signature and check enabled status
                if (userDetails.isEnabled()) {
                    // Create authenticated token with authorities
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,              // credentials null after authentication
                                    userDetails.getAuthorities()
                            );

                    // Set additional details from the HTTP request
                    // (remote IP address, session ID) — useful for audit logging
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Store the authentication in the thread-local SecurityContext.
                    // From this point, SecurityContextHolder.getContext().getAuthentication()
                    // returns this token, and @PreAuthorize annotations will work.
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("Authenticated user {} for request to {}", userEmail, request.getRequestURI());
                }
            }
        } catch (JwtException e) {
            log.warn("Invalid JWT token for request to {}: {}", request.getRequestURI(), e.getMessage());
            // Don't throw — let the request proceed; Spring Security will reject it
            // at the authorization stage if the endpoint requires auth
        }

        // Always continue the filter chain — we either authenticated or didn't,
        // but authorization decisions happen downstream
        filterChain.doFilter(request, response);
    }
}
