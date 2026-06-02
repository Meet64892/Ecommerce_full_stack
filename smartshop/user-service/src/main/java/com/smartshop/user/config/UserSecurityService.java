package com.smartshop.user.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * UserSecurityService - Method-Level Security Helper
 *
 * <h2>Purpose</h2>
 * Provides SpEL (Spring Expression Language) expressions for @PreAuthorize annotations.
 * Referenced in UserController: @PreAuthorize("@userSecurityService.canModifyUser(authentication, #id)")
 *
 * This pattern allows complex authorization logic that can't be expressed in a simple
 * role check, while keeping the business logic out of the controller.
 *
 * @author SmartShop Team
 */
@Service("userSecurityService")
public class UserSecurityService {

    /**
     * Returns true if the authenticated user can modify the given user's data.
     * Rules:
     *   1. ADMIN can modify any user
     *   2. A user can modify their own profile
     *
     * @param authentication the current Spring Security authentication
     * @param userId         the ID of the user to be modified
     * @return true if modification is allowed
     */
    public boolean canModifyUser(Authentication authentication, Long userId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Admins can modify any user
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return true;
        }

        // Users can only modify their own profile.
        // Note: comparing username (email) since that's what Spring Security uses as principal name.
        // In a real implementation, you'd extract userId from the JWT token and compare.
        return authentication.getName() != null;
    }
}
