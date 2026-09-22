package com.bookworm.mapper;

import com.bookworm.dto.*;
import com.bookworm.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookwormMapper {

    private static final LocalDate SAMPLE_DELIVERY = LocalDate.now().plusDays(3);

    public BookSummaryDto toBookSummary(Book book) {
        return new BookSummaryDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCoverImageUrl(),
                book.getDescription(),
                book.getFormat() != null ? book.getFormat().name() : null,
                book.getGenres().stream().map(Genre::getName).collect(Collectors.toList()),
                book.getPrice(),
                "INR",
                SAMPLE_DELIVERY
        );
    }

    public BookDetailDto toBookDetail(Book book) {
        return new BookDetailDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCoverImageUrl(),
                book.getDescription(),
                book.getBackCoverSummary(),
                book.getFormat() != null ? book.getFormat().name() : null,
                book.getGenres().stream().map(Genre::getName).collect(Collectors.toList()),
                book.getPrice(),
                "INR",
                SAMPLE_DELIVERY,
                book.getPublisher(),
                book.getIsbn(),
                book.getLanguage(),
                book.getRatingAverage(),
                book.getRatingCount(),
                book.getTotalSold(),
                book.getAuthorBio()
        );
    }

    public CartItemDto toCartItemDto(CartItem item) {
        Book book = item.getBook();
        return new CartItemDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCoverImageUrl(),
                book.getFormat() != null ? book.getFormat().name() : null,
                book.getGenres().stream().map(Genre::getName).collect(Collectors.toList()),
                item.getUnitPrice(),
                item.getQuantity(),
                SAMPLE_DELIVERY
        );
    }

    public CartItemDto fromOrderItem(OrderItem item) {
        Book book = item.getBook();
        return new CartItemDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCoverImageUrl(),
                book.getFormat() != null ? book.getFormat().name() : null,
                book.getGenres().stream().map(Genre::getName).collect(Collectors.toList()),
                item.getUnitPrice(),
                item.getQuantity(),
                null
        );
    }

    public CartDto toCartDto(Cart cart) {
        List<CartItemDto> items = cart.getItems().stream()
                .map(this::toCartItemDto)
                .collect(Collectors.toList());
        BigDecimal subtotal = items.stream()
                .map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartDto(cart.getId(), items, items.size(), subtotal, "INR");
    }

    public ReviewDto toReviewDto(Review review) {
        String name = review.getUser().getFirstName() != null
                ? review.getUser().getFirstName() + " " + review.getUser().getLastName()
                : review.getUser().getEmail();
        return new ReviewDto(review.getId(), name, review.getRating(), review.getBody(), review.getCreatedAt());
    }

    public AddressDto toAddressDto(Address address) {
        return new AddressDto(
                address.getId(),
                address.getFirstName(),
                address.getLastName(),
                address.getAddressLine(),
                address.getCity(),
                address.getState(),
                address.getPin(),
                address.getCountry(),
                address.getEmail(),
                address.getPhoneCountryCode(),
                address.getPhoneNumber(),
                address.getIsSaved()
        );
    }

    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getSlug(), category.getDisplayName());
    }

    public OrderDto toOrderDto(Order order) {
        List<CartItemDto> items = order.getItems().stream()
                .map(this::fromOrderItem)
                .collect(Collectors.toList());
        return new OrderDto(
                order.getId(),
                order.getStatus().name(),
                items,
                order.getTotalAmount(),
                order.getCurrency(),
                order.getDeliveryAddress() != null ? toAddressDto(order.getDeliveryAddress()) : null,
                order.getPaymentMethod(),
                order.getCreatedAt(),
                null
        );
    }
}
