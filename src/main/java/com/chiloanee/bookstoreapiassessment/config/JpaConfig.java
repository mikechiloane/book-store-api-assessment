package com.chiloanee.bookstoreapiassessment.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import lombok.extern.slf4j.Slf4j;


@Configuration
@Slf4j
public class JpaConfig {

    @EventListener(ApplicationReadyEvent.class)
    public void logDatabaseInfo(ApplicationReadyEvent event) {
        try {
            DataSource dataSource = event.getApplicationContext().getBean(DataSource.class);
            String url = dataSource.getConnection().getMetaData().getURL();
            if (url.contains("h2")) {
                log.info(" Application successfully started with H2 in-memory database");
            } else if (url.contains("mysql")) {
                log.info(" Application successfully started with MySQL database");
            }
        } catch (SQLException e) {
            log.warn("Could not determine database type: {}", e.getMessage());
        }
    }
}
