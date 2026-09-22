package com.bookworm.controller;

import com.bookworm.dto.*;
import com.bookworm.service.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @GetMapping("/summary")
    public ResponseEntity<CheckoutSummaryDto> getSummary(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(checkoutService.getSummary(userDetails.getUsername()));
    }

    @PostMapping("/coupon")
    public ResponseEntity<CheckoutSummaryDto> applyCoupon(
            @Valid @RequestBody CouponRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(checkoutService.applyCoupon(userDetails.getUsername(), request.couponCode()));
    }

    @GetMapping("/addresses")
    public ResponseEntity<Map<String, List<AddressDto>>> listAddresses(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(Map.of("addresses", checkoutService.listAddresses(userDetails.getUsername())));
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressDto> addAddress(
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(checkoutService.addAddress(userDetails.getUsername(), request));
    }
}
