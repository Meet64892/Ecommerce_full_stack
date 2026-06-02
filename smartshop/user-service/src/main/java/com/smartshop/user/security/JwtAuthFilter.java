package com.smartshop.user.security;

import com.smartshop.user.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JwtAuthFilter - Validates bearer token in security filter chain.
 *
 * <h2>Purpose</h2>
 * Spring Security uses a filter chain where each filter can authenticate or reject requests.
 * This filter plugs into that chain before UsernamePasswordAuthenticationFilter to build an
 * authenticated security context from JWT claims.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>OncePerRequestFilter: guarantees single execution per request lifecycle.</li>
 *   <li>SecurityContext: thread-local authenticated principal holder.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Applied to protected endpoints, enabling stateless authorization checks.
 *
 * @see SecurityConfig
 * @author SmartShop Team
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Extracts and validates JWT from Authorization header.
     *
     * @param request servlet request
     * @param response servlet response
     * @param filterChain remaining filters
     * @throws ServletException when filter chain fails
     * @throws IOException when IO errors occur
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        final Claims claims = jwtService.extractClaims(token);
        final String email = claims.getSubject();

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (jwtService.validateToken(token, userDetails.getUsername())) {
                final UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
