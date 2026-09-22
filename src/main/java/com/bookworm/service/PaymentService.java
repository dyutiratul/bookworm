package com.bookworm.service;

import com.bookworm.dto.*;
import com.bookworm.entity.*;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.exception.UnprocessableException;
import com.bookworm.mapper.BookwormMapper;
import com.bookworm.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final BookwormMapper mapper;

    public PaymentService(PaymentRepository paymentRepository,
                          OrderRepository orderRepository,
                          AddressRepository addressRepository,
                          CartRepository cartRepository,
                          UserRepository userRepository,
                          BookwormMapper mapper) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public PaymentResponseDto initiatePayment(String email, PaymentRequest request) {
        User user = getUser(email);
        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        Payment.PaymentMethod method;
        try {
            method = Payment.PaymentMethod.valueOf(request.paymentMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnprocessableException("Invalid payment method: " + request.paymentMethod());
        }

        // Create Order from cart
        Cart cart = cartRepository.findByUserWithItems(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart is empty"));

        Order order = Order.builder()
                .user(user)
                .status(Order.OrderStatus.PLACED)
                .totalAmount(request.amount())
                .currency("INR")
                .deliveryAddress(address)
                .paymentMethod(method.name())
                .build();

        cart.getItems().forEach(ci -> {
            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .book(ci.getBook())
                    .quantity(ci.getQuantity())
                    .unitPrice(ci.getUnitPrice())
                    .build();
            order.getItems().add(oi);
        });

        Order savedOrder = orderRepository.save(order);

        Payment payment = Payment.builder()
                .user(user)
                .order(savedOrder)
                .status(Payment.PaymentStatus.PENDING)
                .paymentMethod(method)
                .payableAmount(request.amount())
                .currency("INR")
                .build();

        Payment saved = paymentRepository.save(payment);

        // Clear cart after order creation
        cart.getItems().clear();
        cartRepository.save(cart);

        return new PaymentResponseDto(saved.getId(), saved.getStatus().name(),
                saved.getPayableAmount(), saved.getCurrency(), saved.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public PaymentStatusDto getPaymentStatus(String email, UUID paymentId) {
        User user = getUser(email);
        Payment payment = paymentRepository.findByIdAndUserId(paymentId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        UUID orderId = payment.getOrder() != null ? payment.getOrder().getId() : null;
        String message = payment.getStatus() == Payment.PaymentStatus.SUCCESS
                ? "Your purchase of the following reads is successful"
                : null;

        return new PaymentStatusDto(payment.getId(), payment.getStatus().name(), orderId, message);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
