package com.bookworm.dto;

import java.util.List;

public record ReviewListResponse(
        List<ReviewDto> reviews,
        PaginationDto pagination
) {}
