# Book Store API Assessment

This is a 3-hour coding assessment completed on Wednesday, July 2025, presented by Vaughn.

A simple REST API for managing books in a bookstore using Spring Boot.

## What it does

- Add, view, update, and delete books
- Search for books by title or author
- Auto-generates ISBN numbers for new books
- Generate dummy books for testing
- Has proper validation and error handling

## Technology Used

- Java 17
- Spring Boot
- MySQL database (or H2 for testing)
- Maven

## How to Run

1. Make sure you have Java 17 and Maven installed
2. Run this command:
```bash
mvn spring-boot:run
```
3. The API will start at `http://localhost:8080`

## API Endpoints

- `POST /books` - Add a new book
- `GET /books` - Get all books
- `GET /books/{id}` - Get a specific book
- `PUT /books/{id}` - Update a book
- `DELETE /books/{id}` - Delete a book
- `GET /books/search` - Search books by title or author

## Example Usage

Add a book:
```bash
curl -X POST http://localhost:8080/books \
  -H "Content-Type: application/json" \
  -d '{"title": "Java Programming", "author": "John Doe"}'
```

Get all books:
```bash
curl http://localhost:8080/books
```

## Testing

Run tests with:
```bash
mvn test
```

## API Documentation

View the Swagger documentation at: `http://localhost:8080/swagger-ui.html`
