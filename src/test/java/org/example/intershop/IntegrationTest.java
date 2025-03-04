package org.example.intershop;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

// Общие настройки и т.д. для всех интеграционных тестов
@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationTest {

    // Using Singleton DB Container for all tests
    static PostgreSQLContainer postgres = new PostgreSQLContainer( "postgres:17.2-alpine3.20");

    // Start containers and uses Ryuk Container to remove containers when JVM process running the tests exited
    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add( "spring.datasource.url", postgres::getJdbcUrl);
        registry.add( "spring.datasource.username", postgres::getUsername);
        registry.add( "spring.datasource.password", postgres::getPassword);
    }

}
