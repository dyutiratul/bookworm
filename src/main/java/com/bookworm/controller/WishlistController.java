package com.bookworm.controller;

import com.bookworm.dto.*;
import com.bookworm.service.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<BookListResponse> getWishlist(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(wishlistService.getWishlist(userDetails.getUsername()));
    }

    @PostMapping("/{bookId}")
    public ResponseEntity<Void> addToWishlist(
            @PathVariable UUID bookId,
            @AuthenticationPrincipal UserDetails userDetails) {
        wishlistService.addToWishlist(userDetails.getUsername(), bookId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable UUID bookId,
            @AuthenticationPrincipal UserDetails userDetails) {
        wishlistService.removeFromWishlist(userDetails.getUsername(), bookId);
        return ResponseEntity.noContent().build();
    }
}
