package com.smartshop.order.security;

import java.util.UUID;

public record MarketplacePrincipal(String email, String role, UUID userId, UUID brandId) {
    public boolean isSuperAdmin() {
        return "SUPER_ADMIN".equals(role);
    }

    public boolean isVendor() {
        return "SUPER_USER".equals(role);
    }
}
