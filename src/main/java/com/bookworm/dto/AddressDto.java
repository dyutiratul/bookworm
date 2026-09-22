package com.bookworm.dto;

import java.util.UUID;

public record AddressDto(
        UUID addressId,
        String firstName,
        String lastName,
        String addressLine,
        String city,
        String state,
        String pin,
        String country,
        String email,
        String phoneCountryCode,
        String phoneNumber,
        Boolean isSaved
) {}
