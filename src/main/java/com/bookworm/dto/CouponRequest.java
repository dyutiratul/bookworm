package com.bookworm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CouponRequest(
        @NotBlank @Size(max = 50) String couponCode
) {}
