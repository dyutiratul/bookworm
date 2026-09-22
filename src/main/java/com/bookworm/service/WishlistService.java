package com.bookworm.service;

import com.bookworm.dto.*;
import com.bookworm.entity.*;
import com.bookworm.exception.ConflictException;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.mapper.BookwormMapper;
import com.bookworm.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookwormMapper mapper;

    public WishlistService(WishlistRepository wishlistRepository,
                           BookRepository bookRepository,
                           UserRepository userRepository,
                           BookwormMapper mapper) {
        this.wishlistRepository = wishlistRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public BookListResponse getWishlist(String email) {
        User user = getUser(email);
        List<BookSummaryDto> books = wishlistRepository.findByUserId(user.getId()).stream()
                .map(w -> mapper.toBookSummary(w.getBook()))
                .collect(Collectors.toList());
        PaginationDto pagination = new PaginationDto(1, books.size(), books.size(), 1);
        return new BookListResponse(books, pagination);
    }

    public void addToWishlist(String email, UUID bookId) {
        User user = getUser(email);
        if (wishlistRepository.existsByUserIdAndBookId(user.getId(), bookId)) {
            throw new ConflictException("Book already in wishlist");
        }
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));
        WishlistItem item = WishlistItem.builder().user(user).book(book).build();
        wishlistRepository.save(item);
    }

    public void removeFromWishlist(String email, UUID bookId) {
        User user = getUser(email);
        WishlistItem item = wishlistRepository.findByUserIdAndBookId(user.getId(), bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not in wishlist"));
        wishlistRepository.delete(item);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
