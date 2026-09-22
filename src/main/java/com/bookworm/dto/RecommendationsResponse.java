package com.bookworm.dto;

import java.util.List;

public record RecommendationsResponse(
        List<BookSummaryDto> recommendedForYou,
        List<BookSummaryDto> bestsellersThisMonth,
        List<BookSummaryDto> newLaunches
) {}
