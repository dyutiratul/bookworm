package com.bookworm.dto;

import java.util.UUID;

public record PaymentStatusDto(
        UUID paymentId,
        String status,
        UUID orderId,
        String message
) {}
