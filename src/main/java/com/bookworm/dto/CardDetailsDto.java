package com.bookworm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Card details DTO.
 * NOTE: Card numbers MUST be tokenised by the client before transmission.
 * Raw card numbers must never be stored — this DTO is for pass-through to the payment gateway only.
 */
public record CardDetailsDto(
        @NotBlank @Pattern(regexp = "^\\d{16}$")           String cardNumber,
        @NotBlank                                           String nameOnCard,
        @NotBlank @Pattern(regexp = "^\\d{3,4}$")          String cvv,
        @NotBlank @Pattern(regexp = "^\\d{2}/\\d{4}$")     String expiryDate
) {}
