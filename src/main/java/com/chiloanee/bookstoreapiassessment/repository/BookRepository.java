package com.chiloanee.bookstoreapiassessment.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chiloanee.bookstoreapiassessment.entity.Book;


@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    

    Optional<Book> findByIsbn(String isbn);
    

    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%')))")
    Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
            @Param("title") String title, 
            @Param("author") String author, 
            Pageable pageable);
}
