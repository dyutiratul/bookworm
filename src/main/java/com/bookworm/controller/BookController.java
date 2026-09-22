package com.bookworm.controller;

import com.bookworm.dto.*;
import com.bookworm.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookDetailDto> getBook(@PathVariable UUID bookId) {
        return ResponseEntity.ok(bookService.getBook(bookId));
    }

    @GetMapping("/{bookId}/related")
    public ResponseEntity<BookListResponse> getRelatedBooks(
            @PathVariable UUID bookId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(bookService.getRelatedBooks(bookId, limit));
    }

    @GetMapping("/{bookId}/reviews")
    public ResponseEntity<ReviewListResponse> getReviews(
            @PathVariable UUID bookId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(bookService.getReviews(bookId, page, limit));
    }

    @PostMapping("/{bookId}/reviews")
    public ResponseEntity<ReviewDto> submitReview(
            @PathVariable UUID bookId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReviewDto dto = bookService.submitReview(bookId, request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
