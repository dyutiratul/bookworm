package com.bookworm.repository;

import com.bookworm.entity.Order;
import com.bookworm.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Page<Order> findByUser(User user, Pageable pageable);

    @Query("""
        SELECT o FROM Order o
        LEFT JOIN FETCH o.items oi
        LEFT JOIN FETCH oi.book
        WHERE o.id = :id AND o.user = :user
        """)
    Optional<Order> findByIdAndUserWithItems(@Param("id") UUID id, @Param("user") User user);
}
