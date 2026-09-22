package com.bookworm.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
        @NotNull UUID addressId,
        @NotBlank String paymentMethod,   // credit_card | debit_card | upi | wallet
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @Valid CardDetailsDto creditCardDetails,
        @Valid CardDetailsDto debitCardDetails,
        String upiId,
        String walletType
) {}
