package com.chiloanee.bookstoreapiassessment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.chiloanee.bookstoreapiassessment.dto.BookRequestDto;
import com.chiloanee.bookstoreapiassessment.dto.BookResponseDto;


public interface BookService {
    

    BookResponseDto createBook(BookRequestDto bookRequestDto);
    

    BookResponseDto getBookById(Long id);
    

    BookResponseDto updateBook(Long id, BookRequestDto bookRequestDto);
    

    void deleteBook(Long id);
    

    Page<BookResponseDto> getAllBooks(Pageable pageable);
    

    Page<BookResponseDto> searchBooks(String title, String author, Pageable pageable);
    

    String generateDummyBooks(int count);
}
