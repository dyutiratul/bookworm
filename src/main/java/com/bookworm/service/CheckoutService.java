package com.bookworm.service;

import com.bookworm.dto.*;
import com.bookworm.entity.*;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.exception.UnprocessableException;
import com.bookworm.mapper.BookwormMapper;
import com.bookworm.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CheckoutService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.12");

    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final BookwormMapper mapper;

    public CheckoutService(CartRepository cartRepository,
                           AddressRepository addressRepository,
                           UserRepository userRepository,
                           BookwormMapper mapper) {
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public CheckoutSummaryDto getSummary(String email) {
        return buildSummary(email, null, BigDecimal.ZERO);
    }

    public CheckoutSummaryDto applyCoupon(String email, String couponCode) {
        // Placeholder: extend with a real coupon table / service
        if (!"SAVE100".equalsIgnoreCase(couponCode)) {
            throw new UnprocessableException("Invalid or expired coupon: " + couponCode);
        }
        return buildSummary(email, couponCode, new BigDecimal("100.00"));
    }

    @Transactional(readOnly = true)
    public List<AddressDto> listAddresses(String email) {
        User user = getUser(email);
        return addressRepository.findByUser(user).stream()
                .map(mapper::toAddressDto)
                .collect(Collectors.toList());
    }

    public AddressDto addAddress(String email, AddressRequest request) {
        User user = getUser(email);
        Address address = Address.builder()
                .user(user)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .addressLine(request.addressLine())
                .city(request.city())
                .state(request.state())
                .pin(request.pin())
                .country(request.country())
                .email(request.email())
                .phoneCountryCode(request.phoneCountryCode())
                .phoneNumber(request.phoneNumber())
                .isSaved(Boolean.TRUE.equals(request.saveAddress()))
                .build();
        return mapper.toAddressDto(addressRepository.save(address));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private CheckoutSummaryDto buildSummary(String email, String couponCode, BigDecimal discount) {
        User user = getUser(email);
        Cart cart = cartRepository.findByUserWithItems(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        List<CartItemDto> items = cart.getItems().stream()
                .map(mapper::toCartItemDto).collect(Collectors.toList());

        BigDecimal subtotal = items.stream()
                .map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal delivery = BigDecimal.ZERO;
        BigDecimal total = subtotal.add(tax).add(delivery).subtract(discount).max(BigDecimal.ZERO);

        return new CheckoutSummaryDto(items, subtotal, tax, delivery, couponCode, discount, total, "INR");
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
