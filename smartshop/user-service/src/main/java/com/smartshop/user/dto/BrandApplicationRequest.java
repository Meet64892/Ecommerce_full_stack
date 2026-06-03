package com.smartshop.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BrandApplicationRequest(
        @NotBlank @Size(max = 200) String brandName,
        @Size(max = 2000) String description
) {
}
