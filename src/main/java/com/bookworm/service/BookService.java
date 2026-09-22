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
public class BookService {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookwormMapper mapper;

    public BookService(BookRepository bookRepository,
                       ReviewRepository reviewRepository,
                       UserRepository userRepository,
                       BookwormMapper mapper) {
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public BookDetailDto getBook(UUID bookId) {
        Book book = bookRepository.findByIdWithDetails(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));
        return mapper.toBookDetail(book);
    }

    public BookListResponse getRelatedBooks(UUID bookId, int limit) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Book not found: " + bookId);
        }
        Pageable pageable = PageRequest.of(0, limit);
        Page<Book> related = bookRepository.findRelatedBooks(bookId, pageable);
        List<BookSummaryDto> books = related.getContent().stream()
                .map(mapper::toBookSummary).collect(Collectors.toList());
        PaginationDto pagination = new PaginationDto(1, limit, related.getTotalElements(), related.getTotalPages());
        return new BookListResponse(books, pagination);
    }

    public ReviewListResponse getReviews(UUID bookId, int page, int limit) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Book not found: " + bookId);
        }
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        Page<Review> reviews = reviewRepository.findByBookId(bookId, pageable);
        List<ReviewDto> dtos = reviews.getContent().stream()
                .map(mapper::toReviewDto).collect(Collectors.toList());
        PaginationDto pagination = new PaginationDto(page, limit, reviews.getTotalElements(), reviews.getTotalPages());
        return new ReviewListResponse(dtos, pagination);
    }

    @Transactional
    public ReviewDto submitReview(UUID bookId, ReviewRequest request, String email) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Review review = Review.builder()
                .book(book)
                .user(user)
                .rating(request.rating())
                .body(request.body())
                .build();
        Review saved = reviewRepository.save(review);
        return mapper.toReviewDto(saved);
    }
}
