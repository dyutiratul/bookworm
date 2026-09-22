package com.bookworm.dto;

import java.util.List;

public record BookListResponse(
        List<BookSummaryDto> books,
        PaginationDto pagination
) {}
