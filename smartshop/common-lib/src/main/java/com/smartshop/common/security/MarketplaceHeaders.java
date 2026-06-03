package com.smartshop.common.security;

/**
 * HTTP headers propagated from the API gateway after JWT validation.
 */
public final class MarketplaceHeaders {
    public static final String USER_ID = "X-User-Id";
    public static final String USER_ROLE = "X-User-Role";
    public static final String BRAND_ID = "X-Brand-Id";
    public static final String USER_EMAIL = "X-User-Email";

    private MarketplaceHeaders() {
    }
}
