package com.chiloanee.bookstoreapiassessment.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.chiloanee.bookstoreapiassessment.dto.BookRequestDto;
import com.chiloanee.bookstoreapiassessment.dto.BookResponseDto;
import com.chiloanee.bookstoreapiassessment.entity.Book;
import com.chiloanee.bookstoreapiassessment.exception.BookNotFoundException;
import com.chiloanee.bookstoreapiassessment.mapper.BookMapper;
import com.chiloanee.bookstoreapiassessment.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookRequestDto bookRequestDto;
    private BookResponseDto bookResponseDto;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("9780306406157");

        bookRequestDto = new BookRequestDto();
        bookRequestDto.setTitle("Test Book");
        bookRequestDto.setAuthor("Test Author");

        bookResponseDto = new BookResponseDto();
        bookResponseDto.setId(1L);
        bookResponseDto.setTitle("Test Book");
        bookResponseDto.setAuthor("Test Author");
        bookResponseDto.setIsbn("9780306406157");
    }

    @Test
    void createBook_ShouldReturnBookResponseDto() {
        when(bookMapper.toEntity(bookRequestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toResponseDto(book)).thenReturn(bookResponseDto);

        BookResponseDto result = bookService.createBook(bookRequestDto);

        assertNotNull(result);
        assertEquals(bookResponseDto.getId(), result.getId());
        assertEquals(bookResponseDto.getTitle(), result.getTitle());
        assertEquals(bookResponseDto.getAuthor(), result.getAuthor());
        assertEquals(bookResponseDto.getIsbn(), result.getIsbn());

        verify(bookMapper).toEntity(bookRequestDto);
        verify(bookRepository).save(book);
        verify(bookMapper).toResponseDto(book);
    }

    @Test
    void getBookById_WhenBookExists_ShouldReturnBookResponseDto() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toResponseDto(book)).thenReturn(bookResponseDto);

        BookResponseDto result = bookService.getBookById(1L);

        assertNotNull(result);
        assertEquals(bookResponseDto.getId(), result.getId());
        assertEquals(bookResponseDto.getTitle(), result.getTitle());

        verify(bookRepository).findById(1L);
        verify(bookMapper).toResponseDto(book);
    }

    @Test
    void getBookById_WhenBookNotExists_ShouldThrowBookNotFoundException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(1L));
        verify(bookRepository).findById(1L);
        verify(bookMapper, never()).toResponseDto(any());
    }

    @Test
    void updateBook_WhenBookExists_ShouldReturnUpdatedBookResponseDto() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toResponseDto(book)).thenReturn(bookResponseDto);

        BookResponseDto result = bookService.updateBook(1L, bookRequestDto);

        assertNotNull(result);
        assertEquals(bookResponseDto.getId(), result.getId());

        verify(bookRepository).findById(1L);
        verify(bookMapper).updateEntity(book, bookRequestDto);
        verify(bookRepository).save(book);
        verify(bookMapper).toResponseDto(book);
    }

    @Test
    void updateBook_WhenBookNotExists_ShouldThrowBookNotFoundException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(1L, bookRequestDto));
        verify(bookRepository).findById(1L);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void deleteBook_WhenBookExists_ShouldDeleteBook() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        bookService.deleteBook(1L);

        verify(bookRepository).existsById(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void deleteBook_WhenBookNotExists_ShouldThrowBookNotFoundException() {
        when(bookRepository.existsById(1L)).thenReturn(false);

        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(1L));
        verify(bookRepository).existsById(1L);
        verify(bookRepository, never()).deleteById(anyLong());
    }

    @Test
    void getAllBooks_ShouldReturnPageOfBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(Arrays.asList(book));
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toResponseDto(book)).thenReturn(bookResponseDto);

        Page<BookResponseDto> result = bookService.getAllBooks(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(bookResponseDto.getId(), result.getContent().get(0).getId());

        verify(bookRepository).findAll(pageable);
    }

    @Test
    void searchBooks_ShouldReturnPageOfBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(Arrays.asList(book));
        when(bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase("Test", "Author", pageable))
                .thenReturn(bookPage);
        when(bookMapper.toResponseDto(book)).thenReturn(bookResponseDto);

        Page<BookResponseDto> result = bookService.searchBooks("Test", "Author", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        verify(bookRepository).findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase("Test", "Author", pageable);
    }
    
    @Test
    void generateDummyBooks_ShouldCreateSpecifiedNumberOfBooks() {
        List<Book> dummyBooks = Arrays.asList(book, book, book);
        when(bookRepository.saveAll(any())).thenReturn(dummyBooks);

        String result = bookService.generateDummyBooks(3);

        assertNotNull(result);
        assertEquals("Successfully generated 3 dummy books", result);
        verify(bookRepository).saveAll(any());
    }
}
