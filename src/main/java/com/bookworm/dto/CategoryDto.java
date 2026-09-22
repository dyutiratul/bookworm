package com.bookworm.dto;

import java.util.UUID;

public record CategoryDto(
        UUID id,
        String slug,
        String displayName
) {}
