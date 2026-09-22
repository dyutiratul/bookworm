package com.bookworm.dto;

import java.math.BigDecimal;
import java.util.List;

public record CheckoutSummaryDto(
        List<CartItemDto> items,
        BigDecimal priceSubtotal,
        BigDecimal tax,
        BigDecimal deliveryCharges,
        String couponCode,
        BigDecimal discount,
        BigDecimal totalAmount,
        String currency
) {}
