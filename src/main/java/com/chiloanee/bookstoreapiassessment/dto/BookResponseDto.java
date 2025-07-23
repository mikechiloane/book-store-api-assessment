package com.chiloanee.bookstoreapiassessment.dto;

import lombok.Data;


@Data
public class BookResponseDto {
    private Long id;
    private String title;
    private String author;
    private String isbn;
}
