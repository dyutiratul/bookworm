package com.bookworm.dto;

import java.time.Instant;
import java.util.UUID;

public record ReviewDto(
        UUID reviewId,
        String reviewerName,
        Integer rating,
        String body,
        Instant createdAt
) {}
