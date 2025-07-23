package com.chiloanee.bookstoreapiassessment.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.chiloanee.bookstoreapiassessment.dto.BookRequestDto;
import com.chiloanee.bookstoreapiassessment.dto.BookResponseDto;
import com.chiloanee.bookstoreapiassessment.entity.Book;
import com.chiloanee.bookstoreapiassessment.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@AutoConfigureMockMvc
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBook_ShouldCreateAndReturnBook() throws Exception {
        // Given
        BookRequestDto bookRequestDto = new BookRequestDto();
        bookRequestDto.setTitle("Integration Test Book");
        bookRequestDto.setAuthor("Integration Author");

        // When & Then
        String responseContent = mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration Test Book"))
                .andExpect(jsonPath("$.author").value("Integration Author"))
                .andExpect(jsonPath("$.isbn").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        BookResponseDto responseDto = objectMapper.readValue(responseContent, BookResponseDto.class);

        // Verify book is saved in database
        assertTrue(bookRepository.existsById(responseDto.getId()));
        
        Book savedBook = bookRepository.findById(responseDto.getId()).orElse(null);
        assertNotNull(savedBook);
        assertEquals("Integration Test Book", savedBook.getTitle());
        assertEquals("Integration Author", savedBook.getAuthor());
        assertNotNull(savedBook.getIsbn());
        assertEquals(13, savedBook.getIsbn().length());
    }

    @Test
    void getAllBooks_ShouldReturnAllBooks() throws Exception {
        // Given
        Book book1 = new Book();
        book1.setTitle("Book 1");
        book1.setAuthor("Author 1");
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setTitle("Book 2");
        book2.setAuthor("Author 2");
        bookRepository.save(book2);

        // When & Then
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title").exists())
                .andExpect(jsonPath("$.content[1].title").exists());
    }
}
