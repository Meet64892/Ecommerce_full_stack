package com.smartshop.user.dto;

import com.smartshop.user.entity.Role;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for Super Admin role changes.
 */
public record UpdateUserRoleRequest(@NotNull Role role) {}
