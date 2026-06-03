package com.smartshop.product.security;

import com.smartshop.common.security.MarketplaceHeaders;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class MarketplaceHeaderFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String role = request.getHeader(MarketplaceHeaders.USER_ROLE);
            if (role != null) {
                UUID userId = parseUuid(request.getHeader(MarketplaceHeaders.USER_ID));
                UUID brandId = parseUuid(request.getHeader(MarketplaceHeaders.BRAND_ID));
                MarketplaceContext.set(new MarketplacePrincipal(
                        request.getHeader(MarketplaceHeaders.USER_EMAIL),
                        role,
                        userId,
                        brandId));
            }
            filterChain.doFilter(request, response);
        } finally {
            MarketplaceContext.clear();
        }
    }

    private UUID parseUuid(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return UUID.fromString(value);
    }
}
