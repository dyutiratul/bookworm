package com.bookworm.dto;

public record PaginationDto(
        int page,
        int limit,
        long totalItems,
        int totalPages
) {}
