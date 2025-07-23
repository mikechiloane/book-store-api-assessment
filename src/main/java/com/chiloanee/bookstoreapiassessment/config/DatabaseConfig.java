package com.chiloanee.bookstoreapiassessment.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

import lombok.extern.slf4j.Slf4j;


//@Configuration
@Slf4j
public class DatabaseConfig {

    private final ConfigurableEnvironment environment;
    
    public DatabaseConfig(ConfigurableEnvironment environment) {
        this.environment = environment;
    }


    @SuppressWarnings("unused")
    private boolean isMySQLAvailable() {
        try {
            String url = "jdbc:mysql://localhost:3306/";
            String username = "root";
            String password = "password";
            
            // Test connection to MySQL - connection is auto-closed by try-with-resources
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                // Connection successful, MySQL is available
                log.info("MySQL database is available, using MySQL configuration");
                return true;
            }
        } catch (SQLException e) {
            log.warn("MySQL database is not available: {}. Falling back to H2 in-memory database", e.getMessage());
            return false;
        }
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();
        
        if (isMySQLAvailable()) {
            // MySQL configuration
            properties.setUrl("jdbc:mysql://localhost:3306/bookstore_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC");
            properties.setUsername("root");
            properties.setPassword("password");
            properties.setDriverClassName("com.mysql.cj.jdbc.Driver");
            
            // Set JPA properties for MySQL
            setJpaPropertiesForMySQL();
            log.info("Configured to use MySQL database");
        } else {
            // H2 in-memory configuration
            properties.setUrl("jdbc:h2:mem:bookstore_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
            properties.setUsername("sa");
            properties.setPassword("");
            properties.setDriverClassName("org.h2.Driver");
            
            // Set JPA properties for H2
            setJpaPropertiesForH2();
            log.info("Configured to use H2 in-memory database");
        }
        
        return properties;
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }
    
    private void setJpaPropertiesForMySQL() {
        Properties props = new Properties();
        props.setProperty("spring.jpa.hibernate.ddl-auto", "update");
        props.setProperty("spring.jpa.show-sql", "true");
        props.setProperty("spring.jpa.properties.hibernate.format_sql", "true");
        props.setProperty("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        props.setProperty("spring.jpa.properties.hibernate.check_nullability", "true");
        
        addPropertiesToEnvironment(props, "mysql-jpa-props");
    }
    
    private void setJpaPropertiesForH2() {
        Properties props = new Properties();
        props.setProperty("spring.jpa.hibernate.ddl-auto", "create-drop");
        props.setProperty("spring.jpa.show-sql", "true");
        props.setProperty("spring.jpa.properties.hibernate.format_sql", "true");
        props.setProperty("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        props.setProperty("spring.jpa.properties.hibernate.check_nullability", "true");
        props.setProperty("spring.h2.console.enabled", "true");
        props.setProperty("spring.h2.console.path", "/h2-console");
        
        addPropertiesToEnvironment(props, "h2-jpa-props");
    }
    
    private void addPropertiesToEnvironment(Properties properties, String name) {
        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.addFirst(new PropertiesPropertySource(name, properties));
    }
}
