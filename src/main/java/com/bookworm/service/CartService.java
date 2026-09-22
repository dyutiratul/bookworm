package com.bookworm.service;

import com.bookworm.dto.*;
import com.bookworm.entity.*;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.mapper.BookwormMapper;
import com.bookworm.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookwormMapper mapper;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       BookRepository bookRepository,
                       UserRepository userRepository,
                       BookwormMapper mapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public CartDto getCart(String email) {
        User user = getUser(email);
        Cart cart = getOrCreateCart(user);
        return mapper.toCartDto(cart);
    }

    public CartDto addItem(String email, CartItemRequest request) {
        User user = getUser(email);
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + request.bookId()));
        Cart cart = getOrCreateCart(user);

        cartItemRepository.findByCartIdAndBookId(cart.getId(), book.getId())
                .ifPresentOrElse(
                        existing -> existing.setQuantity(existing.getQuantity() + request.quantity()),
                        () -> {
                            CartItem item = CartItem.builder()
                                    .cart(cart)
                                    .book(book)
                                    .quantity(request.quantity())
                                    .unitPrice(book.getPrice())
                                    .build();
                            cart.getItems().add(item);
                        });

        cartRepository.save(cart);
        Cart refreshed = cartRepository.findByUserWithItems(user).orElse(cart);
        return mapper.toCartDto(refreshed);
    }

    public CartDto updateItem(String email, UUID bookId, int quantity) {
        User user = getUser(email);
        Cart cart = getOrCreateCart(user);
        CartItem item = cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not in cart"));
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        Cart refreshed = cartRepository.findByUserWithItems(user).orElse(cart);
        return mapper.toCartDto(refreshed);
    }

    public CartDto removeItem(String email, UUID bookId) {
        User user = getUser(email);
        Cart cart = getOrCreateCart(user);
        cartItemRepository.deleteByCartIdAndBookId(cart.getId(), bookId);
        Cart refreshed = cartRepository.findByUserWithItems(user).orElse(cart);
        return mapper.toCartDto(refreshed);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = Cart.builder().user(user).build();
            return cartRepository.save(newCart);
        });
    }
}
