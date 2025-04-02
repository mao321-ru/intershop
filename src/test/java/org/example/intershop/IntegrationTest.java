package org.example.intershop;

import com.redis.testcontainers.RedisContainer;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import reactor.core.publisher.Mono;

// Общие настройки и т.д. для всех интеграционных тестов
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
// таймаут 15 сек ожидания ответа по запросу, т.к. при дефолтном 5 сек возникала ошибка при запуске 10-го автотеста
// с неполным стеком ошибок
@AutoConfigureWebTestClient( timeout = "15000")
public abstract class IntegrationTest {

    // Using Singleton Container for all tests
    static PostgreSQLContainer postgres = new PostgreSQLContainer( "postgres:17.2-alpine3.20");
    static RedisContainer redis = new RedisContainer( "redis:7.4.2-bookworm");

    // Start containers and uses Ryuk Container to remove containers when JVM process running the tests exited
    static {
        postgres.start();
        redis.start();
    }

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add( "spring.r2dbc.url", () ->
            "r2dbc:postgresql://%s:%s/%s".formatted(
                postgres.getHost(),
                postgres.getFirstMappedPort(),
                postgres.getDatabaseName()
            )
        );
        registry.add( "spring.r2dbc.username", postgres::getUsername);
        registry.add( "spring.r2dbc.password", postgres::getPassword);

        registry.add( "spring.data.redis.url", redis::getRedisURI);
    }

    // Решение из https://stackoverflow.com/questions/64115419/how-to-substitute-sql-in-tests-with-spring-data-r2dbc
    // вместо переставшего работать с r2dbc @Sql на уровне класса (восстановление данных перед каждым тестом):
    // @Sql( scripts = {"/test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD )
    @Autowired
    private ConnectionFactory connectionFactory;

    private void executeScriptBlocking(final Resource sqlScript) {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, sqlScript))
                .block();
    }

    @BeforeEach
    private void prepareTestData( @Value( "classpath:/test-data.sql") Resource script) {
        executeScriptBlocking( script);
    }

}
