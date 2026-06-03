package com.smartshop.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BrandApplyRequest(
        @NotBlank @Size(max = 200) String name,
        @NotBlank @Size(max = 2000) String description,
        @NotBlank
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must be lowercase letters, numbers, and hyphens")
        @Size(max = 120)
        String slug) {
}
