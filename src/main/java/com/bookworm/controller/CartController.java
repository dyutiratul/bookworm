package com.bookworm.controller;

import com.bookworm.dto.*;
import com.bookworm.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartDto> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getCart(userDetails.getUsername()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartDto> addItem(
            @Valid @RequestBody CartItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.addItem(userDetails.getUsername(), request));
    }

    @PatchMapping("/items/{bookId}")
    public ResponseEntity<CartDto> updateItem(
            @PathVariable UUID bookId,
            @Valid @RequestBody UpdateCartItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.updateItem(userDetails.getUsername(), bookId, request.quantity()));
    }

    @DeleteMapping("/items/{bookId}")
    public ResponseEntity<CartDto> removeItem(
            @PathVariable UUID bookId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.removeItem(userDetails.getUsername(), bookId));
    }
}
