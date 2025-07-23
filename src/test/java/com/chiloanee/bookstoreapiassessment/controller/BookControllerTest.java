package com.chiloanee.bookstoreapiassessment.controller;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chiloanee.bookstoreapiassessment.dto.BookRequestDto;
import com.chiloanee.bookstoreapiassessment.dto.BookResponseDto;
import com.chiloanee.bookstoreapiassessment.exception.BookNotFoundException;
import com.chiloanee.bookstoreapiassessment.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookRequestDto bookRequestDto;
    private BookResponseDto bookResponseDto;

    @BeforeEach
    void setUp() {
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
    void createBook_ShouldReturnCreatedBook() throws Exception {
        // Given
        when(bookService.createBook(any(BookRequestDto.class))).thenReturn(bookResponseDto);

        // When & Then
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"))
                .andExpect(jsonPath("$.isbn").value("9780306406157"));
    }

    @Test
    void createBook_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        BookRequestDto invalidRequest = new BookRequestDto();
        invalidRequest.setTitle(""); // Invalid: blank title
        invalidRequest.setAuthor("Test Author");

        // When & Then
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookById_WhenBookExists_ShouldReturnBook() throws Exception {
        // Given
        when(bookService.getBookById(1L)).thenReturn(bookResponseDto);

        // When & Then
        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"))
                .andExpect(jsonPath("$.isbn").value("9780306406157"));
    }

    @Test
    void getBookById_WhenBookNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(bookService.getBookById(1L)).thenThrow(new BookNotFoundException(1L));

        // When & Then
        mockMvc.perform(get("/books/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBook_WhenBookExists_ShouldReturnUpdatedBook() throws Exception {
        // Given
        when(bookService.updateBook(eq(1L), any(BookRequestDto.class))).thenReturn(bookResponseDto);

        // When & Then
        mockMvc.perform(put("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void deleteBook_WhenBookExists_ShouldReturnNoContent() throws Exception {
        // When & Then
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBook_WhenBookNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        doThrow(new BookNotFoundException(1L)).when(bookService).deleteBook(1L);

        // When & Then
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllBooks_ShouldReturnPageOfBooks() throws Exception {
        // Given
        Page<BookResponseDto> page = new PageImpl<>(Arrays.asList(bookResponseDto), PageRequest.of(0, 10), 1);
        when(bookService.getAllBooks(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }
}
