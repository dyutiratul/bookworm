package com.bookworm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record BookDetailDto(
        UUID bookId,
        String title,
        String author,
        String coverImageUrl,
        String description,
        String backCoverSummary,
        String format,
        List<String> genres,
        BigDecimal price,
        String currency,
        LocalDate deliveryDate,
        String publisher,
        String isbn,
        String language,
        BigDecimal ratingAverage,
        Integer ratingCount,
        Integer totalSold,
        String authorBio
) {}
