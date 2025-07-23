package com.chiloanee.bookstoreapiassessment.dto;

import lombok.Data;


@Data
public class SearchParametersDto {
    private String title;
    private String author;
    private int page = 0;
    private int size = 10;
    private String sortBy = "id";
    private String sortDir = "asc";
}
