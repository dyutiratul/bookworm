package com.bookworm.service;

import com.bookworm.dto.*;
import com.bookworm.entity.*;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.mapper.BookwormMapper;
import com.bookworm.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final BookwormMapper mapper;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        UserRepository userRepository,
                        BookwormMapper mapper) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public OrderListResponse listOrders(String email, int page, int limit) {
        User user = getUser(email);
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        Page<Order> orders = orderRepository.findByUser(user, pageable);
        List<OrderDto> dtos = orders.getContent().stream()
                .map(mapper::toOrderDto)
                .collect(Collectors.toList());
        PaginationDto pagination = new PaginationDto(page, limit, orders.getTotalElements(), orders.getTotalPages());
        return new OrderListResponse(dtos, pagination);
    }

    public OrderDto getOrder(String email, UUID orderId) {
        User user = getUser(email);
        Order order = orderRepository.findByIdAndUserWithItems(orderId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        return mapper.toOrderDto(order);
    }

    @Transactional
    public CartDto buyAgain(String email, UUID orderId, CartService cartService) {
        User user = getUser(email);
        Order order = orderRepository.findByIdAndUserWithItems(orderId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        Cart cart = cartService.getOrCreateCart(user);
        order.getItems().forEach(oi ->
                cartService.addItem(email, new CartItemRequest(oi.getBook().getId(), oi.getQuantity())));
        Cart refreshed = cartRepository.findByUserWithItems(user).orElse(cart);
        return mapper.toCartDto(refreshed);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
