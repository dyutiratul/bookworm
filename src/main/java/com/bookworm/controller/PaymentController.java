package com.bookworm.controller;

import com.bookworm.dto.*;
import com.bookworm.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDto> initiatePayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(paymentService.initiatePayment(userDetails.getUsername(), request));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentStatusDto> getPaymentStatus(
            @PathVariable UUID paymentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(paymentService.getPaymentStatus(userDetails.getUsername(), paymentId));
    }
}
