package com.chiloanee.bookstoreapiassessment.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Book Store API")
                        .description("A comprehensive RESTful API for managing books in a bookstore system.\n\n" +
                                "The Book Store API provides powerful tools for:\n" +
                                "- **Book Management**: Create, read, update, and delete book records\n" +
                                "- **Search & Filter**: Search books by title and author with pagination\n" +
                                "- **ISBN Generation**: Automatic ISBN-13 generation following international standards\n" +
                                "- **Data Validation**: Comprehensive input validation and error handling\n\n" +
                                "## Features\n" +
                                "- **Full CRUD Operations**: Complete book lifecycle management\n" +
                                "- **Idempotent APIs**: All endpoints designed to be idempotent\n" +
                                "- **Auto-generated ISBN-13**: Follows ISBN-13 standards with proper check digit calculation\n" +
                                "- **Pagination & Sorting**: Support for paginated results with sorting options\n" +
                                "- **Search Functionality**: Advanced search by title and/or author\n" +
                                "- **Comprehensive Testing**: Unit tests, integration tests, and controller tests\n\n" )
                        .version("2.0")
                        .contact(new Contact()
                                .name("Mike Chiloane")
                                .email("mike@mikechiloane.co.za"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("http://localhost:8081")
                                .description("Local Development Server (H2 Mode)")))
                .components(new Components());
    }
}
