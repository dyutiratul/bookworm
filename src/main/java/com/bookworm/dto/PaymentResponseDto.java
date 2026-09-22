package com.bookworm.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponseDto(
        UUID paymentId,
        String status,
        BigDecimal payableAmount,
        String currency,
        Instant createdAt
) {}
