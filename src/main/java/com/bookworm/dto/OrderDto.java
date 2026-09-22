package com.bookworm.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderDto(
        UUID orderId,
        String status,
        List<CartItemDto> items,
        BigDecimal totalAmount,
        String currency,
        AddressDto deliveryAddress,
        String paymentMethod,
        Instant placedAt,
        Instant deliveredAt
) {}
