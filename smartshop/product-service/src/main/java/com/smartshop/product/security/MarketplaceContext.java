package com.smartshop.product.security;

public final class MarketplaceContext {
    private static final ThreadLocal<MarketplacePrincipal> CURRENT = new ThreadLocal<>();

    private MarketplaceContext() {
    }

    public static void set(MarketplacePrincipal principal) {
        CURRENT.set(principal);
    }

    public static MarketplacePrincipal get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
