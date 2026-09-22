package com.bookworm.dto;

import java.util.List;

public record OrderListResponse(
        List<OrderDto> orders,
        PaginationDto pagination
) {}
