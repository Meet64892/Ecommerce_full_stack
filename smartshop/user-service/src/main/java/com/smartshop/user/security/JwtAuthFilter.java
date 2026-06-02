package com.smartshop.user.security;

import com.smartshop.user.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthFilter - Plugs JWT validation into Spring Security's servlet filter chain.
 *
 * <h2>Purpose</h2>
 * Spring Security processes requests through ordered filters. This filter runs once per request, reads a bearer token,
 * validates it, and populates SecurityContext so controllers can trust the authenticated principal.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>OncePerRequestFilter: Guarantees one execution even during internal forwards.</li>
 *   <li>SecurityContext: Thread-local holder for the authenticated principal in servlet applications.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * SecurityConfig inserts this filter before UsernamePasswordAuthenticationFilter in the chain.
 *
 * @see SecurityConfig
 * @author SmartShop Team
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Creates the filter with explicit dependencies.
     *
     * @param jwtService token parser and validator
     * @param userDetailsService adapter that loads users as Spring Security principals
     */
    public JwtAuthFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Validates the Authorization header and sets the authentication context when the JWT is valid.
     *
     * @param request current servlet request
     * @param response current servlet response
     * @param filterChain remaining filters and controller dispatch
     * @throws ServletException when downstream filter processing fails
     * @throws IOException when the response cannot be written
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authorization.substring("Bearer ".length());
        String username = jwtService.extractUsername(token);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
