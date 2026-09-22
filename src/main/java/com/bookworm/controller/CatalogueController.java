package com.bookworm.controller;

import com.bookworm.dto.*;
import com.bookworm.service.CatalogueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
public class CatalogueController {

    private final CatalogueService catalogueService;

    public CatalogueController(CatalogueService catalogueService) {
        this.catalogueService = catalogueService;
    }

    @GetMapping("/catalogue")
    public ResponseEntity<BookListResponse> listBooks(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(catalogueService.listBooks(category, language, format, priceMin, priceMax, sortBy, page, limit));
    }

    @GetMapping("/catalogue/search")
    public ResponseEntity<BookListResponse> searchBooks(
            @RequestParam String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String format,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(catalogueService.search(q, category, language, format, page, limit));
    }

    @GetMapping("/catalogue/categories")
    public ResponseEntity<Map<String, List<CategoryDto>>> listCategories() {
        return ResponseEntity.ok(Map.of("categories", catalogueService.listCategories()));
    }
}
