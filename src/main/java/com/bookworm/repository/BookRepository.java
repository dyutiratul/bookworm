package com.bookworm.repository;

import com.bookworm.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID>, JpaSpecificationExecutor<Book> {

    Optional<Book> findBySlug(String slug);

    @Query("""
        SELECT b FROM Book b
        LEFT JOIN FETCH b.genres
        LEFT JOIN FETCH b.category
        WHERE b.id = :id
        """)
    Optional<Book> findByIdWithDetails(@Param("id") UUID id);

    @Query("""
        SELECT b FROM Book b
        LEFT JOIN FETCH b.genres
        WHERE b.category = (SELECT b2.category FROM Book b2 WHERE b2.id = :bookId)
        AND b.id <> :bookId
        """)
    Page<Book> findRelatedBooks(@Param("bookId") UUID bookId, Pageable pageable);

    Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title, String author, String description, Pageable pageable);

    @Query("""
        SELECT b FROM Book b ORDER BY b.totalSold DESC
        """)
    Page<Book> findBestsellers(Pageable pageable);

    @Query("""
        SELECT b FROM Book b ORDER BY b.createdAt DESC
        """)
    Page<Book> findNewLaunches(Pageable pageable);
}
