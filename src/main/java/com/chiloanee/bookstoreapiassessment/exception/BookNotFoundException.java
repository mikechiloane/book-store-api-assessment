package com.chiloanee.bookstoreapiassessment.exception;


public class BookNotFoundException extends RuntimeException {
    
    public BookNotFoundException(String message) {
        super(message);
    }
    
    public BookNotFoundException(Long id) {
        super("Book not found with id: " + id);
    }
}
