package com.bookworm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record BookSummaryDto(
        UUID bookId,
        String title,
        String author,
        String coverImageUrl,
        String description,
        String format,
        List<String> genres,
        BigDecimal price,
        String currency,
        LocalDate deliveryDate
) {}
