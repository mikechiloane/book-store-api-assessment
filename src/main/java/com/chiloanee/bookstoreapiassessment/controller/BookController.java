package com.chiloanee.bookstoreapiassessment.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chiloanee.bookstoreapiassessment.dto.BookRequestDto;
import com.chiloanee.bookstoreapiassessment.dto.BookResponseDto;
import com.chiloanee.bookstoreapiassessment.service.BookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Book Management", description = "APIs for managing books in the bookstore")
public class BookController {
    
    private final BookService bookService;
    
    @Operation(
        summary = "Create a new book", 
        description = "Add a new book to the bookstore. The ISBN will be automatically generated following ISBN-13 standards.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Book details to create",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BookRequestDto.class),
                examples = @ExampleObject(
                    name = "Book Creation Example",
                    value = """
                    {
                        "title": "The Great Gatsby",
                        "author": "F. Scott Fitzgerald"
                    }
                    """
                )
            )
        )
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "201", 
                description = "Book created successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BookResponseDto.class)
                )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<BookResponseDto> createBook(
            @Valid @RequestBody BookRequestDto bookRequestDto) {
        log.info("POST /books - Creating new book");
        BookResponseDto createdBook = bookService.createBook(bookRequestDto);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }
    
    @Operation(
        summary = "Get a book by ID", 
        description = "Retrieve a specific book by its unique identifier",
        parameters = {
            @Parameter(name = "id", description = "Unique book identifier", required = true, example = "1")
        }
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "Book found successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BookResponseDto.class)
                )
            ),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBookById(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        log.info("GET /books/{} - Fetching book by ID", id);
        BookResponseDto book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }
    
    @Operation(summary = "Update a book", description = "Update an existing book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDto> updateBook(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @Valid @RequestBody BookRequestDto bookRequestDto) {
        log.info("PUT /books/{} - Updating book", id);
        BookResponseDto updatedBook = bookService.updateBook(id, bookRequestDto);
        return ResponseEntity.ok(updatedBook);
    }
    
    @Operation(summary = "Delete a book", description = "Delete a book by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        log.info("DELETE /books/{} - Deleting book", id);
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Get all books", description = "Retrieve all books with pagination and sorting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<Page<BookResponseDto>> getAllBooks(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("GET /books - Fetching books with pagination");
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BookResponseDto> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(books);
    }
    
    @Operation(
        summary = "Search books", 
        description = "Search books by title and/or author with pagination support",
        parameters = {
            @Parameter(name = "title", description = "Search by book title (partial matches supported)", example = "Great"),
            @Parameter(name = "author", description = "Search by author name (partial matches supported)", example = "Fitzgerald"),
            @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
            @Parameter(name = "size", description = "Number of items per page", example = "10")
        }
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "Books retrieved successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)
                )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<Page<BookResponseDto>> searchBooks(
            @Parameter(description = "Search by title") @RequestParam(required = false) String title,
            @Parameter(description = "Search by author") @RequestParam(required = false) String author,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /books/search - Searching books");
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BookResponseDto> books = bookService.searchBooks(title, author, pageable);
        return ResponseEntity.ok(books);
    }
    
    @Operation(
        summary = "Generate dummy books", 
        description = "Generate a specified number of dummy books for testing purposes"
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "Dummy books generated successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class)
                )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid count parameter", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/generate-dummy")
    public ResponseEntity<String> generateDummyBooks(
            @Parameter(description = "Number of dummy books to generate") 
            @RequestParam(defaultValue = "10") int count) {
        
        log.info("POST /books/generate-dummy - Generating {} dummy books", count);
        
        if (count <= 0 || count > 100) {
            return ResponseEntity.badRequest().body("Count must be between 1 and 100");
        }
        
        String result = bookService.generateDummyBooks(count);
        return ResponseEntity.ok(result);
    }
}
