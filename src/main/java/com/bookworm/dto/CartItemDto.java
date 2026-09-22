package com.bookworm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CartItemDto(
        UUID bookId,
        String title,
        String author,
        String coverImageUrl,
        String format,
        List<String> genres,
        BigDecimal unitPrice,
        Integer quantity,
        LocalDate deliveryDate
) {}
