package com.chiloanee.bookstoreapiassessment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class BookRequestDto {
    
    @NotBlank(message = "Title cannot be blank")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;
    
    @NotBlank(message = "Author cannot be blank")
    @Size(max = 50, message = "Author cannot exceed 50 characters")
    private String author;
}
