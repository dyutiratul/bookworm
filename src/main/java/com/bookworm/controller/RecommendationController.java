package com.bookworm.controller;

import com.bookworm.dto.*;
import com.bookworm.service.CatalogueService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final CatalogueService catalogueService;

    public RecommendationController(CatalogueService catalogueService) {
        this.catalogueService = catalogueService;
    }

    @GetMapping
    public ResponseEntity<RecommendationsResponse> getRecommendations(
            @RequestParam(required = false) String section,
            @RequestParam(defaultValue = "10") int limit,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(catalogueService.getRecommendations(section, limit));
    }
}
