package com.chiloanee.bookstoreapiassessment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title cannot be blank")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String title;
    
    @NotBlank(message = "Author cannot be blank")
    @Size(max = 50, message = "Author cannot exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String author;
    
    @Column(unique = true, nullable = false, length = 13)
    private String isbn;
    
    @PrePersist
    private void generateIsbn() {
        if (this.isbn == null) {
            this.isbn = generateUniqueIsbn();
        }
    }
    

    private String generateUniqueIsbn() {

        String basePrefix = "978";
        

        StringBuilder isbnBuilder = new StringBuilder(basePrefix);
        for (int i = 0; i < 9; i++) {
            isbnBuilder.append((int) (Math.random() * 10));
        }
        

        String isbnWithoutCheckDigit = isbnBuilder.toString();
        int checkDigit = calculateIsbnCheckDigit(isbnWithoutCheckDigit);
        isbnBuilder.append(checkDigit);
        
        return isbnBuilder.toString();
    }
    

    private int calculateIsbnCheckDigit(String isbnWithoutCheckDigit) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(isbnWithoutCheckDigit.charAt(i));
            if (i % 2 == 0) {
                sum += digit * 1;
            } else {
                sum += digit * 3;
            }
        }
        return (10 - (sum % 10)) % 10;
    }
}
