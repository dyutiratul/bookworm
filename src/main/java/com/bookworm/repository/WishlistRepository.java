package com.bookworm.repository;

import com.bookworm.entity.WishlistItem;
import com.bookworm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<WishlistItem, UUID> {

    @Query("SELECT w FROM WishlistItem w LEFT JOIN FETCH w.book WHERE w.user.id = :userId")
    List<WishlistItem> findByUserId(@Param("userId") UUID userId);

    Optional<WishlistItem> findByUserIdAndBookId(UUID userId, UUID bookId);

    boolean existsByUserIdAndBookId(UUID userId, UUID bookId);
}
