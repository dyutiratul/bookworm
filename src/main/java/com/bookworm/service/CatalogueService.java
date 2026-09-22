package com.bookworm.service;

import com.bookworm.dto.*;
import com.bookworm.entity.*;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.mapper.BookwormMapper;
import com.bookworm.repository.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CatalogueService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookwormMapper mapper;

    public CatalogueService(BookRepository bookRepository,
                            CategoryRepository categoryRepository,
                            BookwormMapper mapper) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    public BookListResponse listBooks(String category, String language, String format,
                                      BigDecimal priceMin, BigDecimal priceMax,
                                      String sortBy, int page, int limit) {
        Pageable pageable = buildPageable(sortBy, page, limit);
        Specification<Book> spec = buildSpec(category, language, format, priceMin, priceMax);
        Page<Book> result = bookRepository.findAll(spec, pageable);
        return toBookListResponse(result, page, limit);
    }

    public BookListResponse search(String q, String category, String language, String format, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Book> result = bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        q, q, q, pageable);
        return toBookListResponse(result, page, limit);
    }

    public List<CategoryDto> listCategories() {
        return categoryRepository.findAll().stream()
                .map(mapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public RecommendationsResponse getRecommendations(String section, int limit) {
        Pageable pg = PageRequest.of(0, limit);
        List<BookSummaryDto> recommended = bookRepository.findAll(pg).stream()
                .map(mapper::toBookSummary).collect(Collectors.toList());
        List<BookSummaryDto> bestsellers = bookRepository.findBestsellers(pg).stream()
                .map(mapper::toBookSummary).collect(Collectors.toList());
        List<BookSummaryDto> newLaunches = bookRepository.findNewLaunches(pg).stream()
                .map(mapper::toBookSummary).collect(Collectors.toList());

        if (section == null) {
            return new RecommendationsResponse(recommended, bestsellers, newLaunches);
        }
        return switch (section) {
            case "recommended_for_you" -> new RecommendationsResponse(recommended, List.of(), List.of());
            case "bestsellers"         -> new RecommendationsResponse(List.of(), bestsellers, List.of());
            case "new_launches"        -> new RecommendationsResponse(List.of(), List.of(), newLaunches);
            default                    -> new RecommendationsResponse(recommended, bestsellers, newLaunches);
        };
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Pageable buildPageable(String sortBy, int page, int limit) {
        Sort sort = switch (sortBy != null ? sortBy : "relevance") {
            case "price_asc"  -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            case "newest"     -> Sort.by("createdAt").descending();
            case "bestseller" -> Sort.by("totalSold").descending();
            default           -> Sort.unsorted();
        };
        return PageRequest.of(page - 1, limit, sort);
    }

    private Specification<Book> buildSpec(String category, String language, String format,
                                          BigDecimal priceMin, BigDecimal priceMax) {
        return (root, query, cb) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            if (category != null) {
                Join<Book, Category> catJoin = root.join("category", JoinType.LEFT);
                predicates.add(cb.equal(catJoin.get("slug"), category));
            }
            if (language != null) predicates.add(cb.equal(root.get("language"), language));
            if (format != null)   predicates.add(cb.equal(root.get("format"), Book.BookFormat.valueOf(format.toUpperCase())));
            if (priceMin != null) predicates.add(cb.greaterThanOrEqualTo(root.get("price"), priceMin));
            if (priceMax != null) predicates.add(cb.lessThanOrEqualTo(root.get("price"), priceMax));
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    private BookListResponse toBookListResponse(Page<Book> page, int pageNum, int limit) {
        List<BookSummaryDto> books = page.getContent().stream()
                .map(mapper::toBookSummary).collect(Collectors.toList());
        PaginationDto pagination = new PaginationDto(pageNum, limit, page.getTotalElements(), page.getTotalPages());
        return new BookListResponse(books, pagination);
    }
}
