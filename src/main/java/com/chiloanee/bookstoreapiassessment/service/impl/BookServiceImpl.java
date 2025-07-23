package com.chiloanee.bookstoreapiassessment.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chiloanee.bookstoreapiassessment.dto.BookRequestDto;
import com.chiloanee.bookstoreapiassessment.dto.BookResponseDto;
import com.chiloanee.bookstoreapiassessment.entity.Book;
import com.chiloanee.bookstoreapiassessment.exception.BookNotFoundException;
import com.chiloanee.bookstoreapiassessment.mapper.BookMapper;
import com.chiloanee.bookstoreapiassessment.repository.BookRepository;
import com.chiloanee.bookstoreapiassessment.service.BookService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookServiceImpl implements BookService {
    
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    
    @Override
    public BookResponseDto createBook(BookRequestDto bookRequestDto) {
        log.info("Creating new book with title: {}", bookRequestDto.getTitle());
        
        Book book = bookMapper.toEntity(bookRequestDto);
        Book savedBook = bookRepository.save(book);
        
        log.info("Book created successfully with ID: {} and ISBN: {}", 
                savedBook.getId(), savedBook.getIsbn());
        
        return bookMapper.toResponseDto(savedBook);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BookResponseDto getBookById(Long id) {
        log.info("Fetching book with ID: {}", id);
        
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        
        return bookMapper.toResponseDto(book);
    }
    
    @Override
    public BookResponseDto updateBook(Long id, BookRequestDto bookRequestDto) {
        log.info("Updating book with ID: {}", id);
        
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        
        bookMapper.updateEntity(existingBook, bookRequestDto);
        Book updatedBook = bookRepository.save(existingBook);
        
        log.info("Book updated successfully with ID: {}", updatedBook.getId());
        
        return bookMapper.toResponseDto(updatedBook);
    }
    
    @Override
    public void deleteBook(Long id) {
        log.info("Deleting book with ID: {}", id);
        
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        
        bookRepository.deleteById(id);
        
        log.info("Book deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<BookResponseDto> getAllBooks(Pageable pageable) {
        log.info("Fetching all books with pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Book> books = bookRepository.findAll(pageable);
        
        return books.map(bookMapper::toResponseDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<BookResponseDto> searchBooks(String title, String author, Pageable pageable) {
        log.info("Searching books with title: '{}' and author: '{}'", title, author);
        
        Page<Book> books = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
                title, author, pageable);
        
        return books.map(bookMapper::toResponseDto);
    }
    
    @Override
    public String generateDummyBooks(int count) {
        log.info("Generating {} dummy books", count);
        

        String[] titles = {
            "The Art of Programming", "Data Structures and Algorithms", "Clean Code", 
            "Design Patterns", "Effective Java", "Spring Boot in Action", "Microservices Patterns",
            "System Design Interview", "The Pragmatic Programmer", "Code Complete",
            "Head First Design Patterns", "Java: The Complete Reference", "Spring Framework Essentials",
            "Database Design Fundamentals", "Web Development with React", "Python Programming",
            "Machine Learning Basics", "Software Engineering Principles", "API Design Best Practices",
            "DevOps Handbook", "Cloud Native Applications", "Kotlin in Action",
            "Advanced Java Programming", "Full Stack Development", "Agile Software Development"
        };
        
        String[] authors = {
            "Robert Martin", "Joshua Bloch", "Martin Fowler", "Gang of Four",
            "Craig Walls", "Chris Richardson", "Alex Xu", "Andy Hunt", "Steve McConnell",
            "Eric Freeman", "Herbert Schildt", "Rod Johnson", "C.J. Date",
            "Dan Abramov", "Guido van Rossum", "Andrew Ng", "Ian Sommerville",
            "Leonard Richardson", "Gene Kim", "Christian Posta", "Dmitry Jemerov",
            "Cay Horstmann", "Kyle Simpson", "Robert Knepper"
        };
        
        Random random = new Random();
        List<Book> dummyBooks = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            Book book = new Book();
            book.setTitle(titles[random.nextInt(titles.length)]);
            book.setAuthor(authors[random.nextInt(authors.length)]);

            dummyBooks.add(book);
        }
        
        List<Book> savedBooks = bookRepository.saveAll(dummyBooks);
        
        log.info("Successfully generated {} dummy books", savedBooks.size());
        
        return String.format("Successfully generated %d dummy books", savedBooks.size());
    }
}
