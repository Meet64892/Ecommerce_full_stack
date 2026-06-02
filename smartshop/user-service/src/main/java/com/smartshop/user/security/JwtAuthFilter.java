package com.smartshop.user.security;

import com.smartshop.user.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthFilter - Per-request filter that authenticates via the Bearer JWT.
 *
 * <h2>Purpose</h2>
 * Even though the gateway validates tokens, defense-in-depth means this service
 * also establishes a {@code SecurityContext} from the JWT so endpoint-level
 * authorization (e.g. only ADMIN can list all users) works locally.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>OncePerRequestFilter</b>: guarantees the filter runs exactly once per
 *       request even across internal forwards/dispatches.</li>
 *   <li><b>Security filter chain</b>: Spring Security is a chain of servlet
 *       filters. We insert this filter BEFORE the username/password filter so a
 *       valid token populates the context and the request is treated as
 *       authenticated.</li>
 *   <li><b>Stateless</b>: we never create an HTTP session; the token IS the
 *       credential on every call.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Registered in {@code SecurityConfig}; runs early in the chain to set the
 * authentication for downstream authorization checks.
 *
 * @see JwtService
 * @author SmartShop Team
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Inspects the Authorization header; if a valid Bearer token is present,
     * sets the authenticated principal in the SecurityContext.
     *
     * @param request     the inbound request
     * @param response    the response
     * @param filterChain the remaining filter chain
     * @throws ServletException on servlet errors
     * @throws IOException      on I/O errors
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // No bearer token -> let the chain continue; access rules decide if the
        // (anonymous) request is allowed (e.g. /auth/login is public).
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        final String username = safeExtractUsername(token);

        // Only authenticate if we have a username AND nobody is authenticated yet.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(token, userDetails.getUsername())) {
                // Build an authenticated token carrying the granted authorities.
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Publish into the context so authorization checks see the user.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the username without throwing if the token is malformed.
     *
     * @param token the JWT
     * @return the username, or null if the token cannot be parsed
     */
    private String safeExtractUsername(String token) {
        try {
            return jwtService.extractUsername(token);
        } catch (Exception e) {
            // Malformed/expired token: treat as anonymous rather than 500.
            return null;
        }
    }
}
