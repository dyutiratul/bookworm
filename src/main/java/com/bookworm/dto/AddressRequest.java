package com.bookworm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotBlank @Size(max = 300) String addressLine,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Size(max = 100) String state,
        @NotBlank @Size(max = 20)  String pin,
        @NotBlank @Size(max = 100) String country,
        @NotBlank @Email           String email,
        @Size(max = 10)            String phoneCountryCode,
        @NotBlank @Size(max = 20)  String phoneNumber,
        Boolean saveAddress
) {}
