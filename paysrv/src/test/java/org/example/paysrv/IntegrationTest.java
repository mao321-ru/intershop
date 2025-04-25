package org.example.paysrv;

import com.jayway.jsonpath.JsonPath;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.assertj.core.api.Assertions.assertThat;

// Общие настройки и т.д. для всех интеграционных тестов
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class IntegrationTest {
    private static final Logger log = LoggerFactory.getLogger( IntegrationTest.class);

    @Autowired
    WebTestClient wtc;

    // Using Singleton Container for all tests
    static KeycloakContainer keycloak = new KeycloakContainer( "quay.io/keycloak/keycloak:26.1.3")
            .withRealmImportFile("/keycloak/paysrv-test.realm.json");

    // Start containers and uses Ryuk Container to remove containers when JVM process running the tests exited
    static {
        keycloak.start();
    }

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add(
            "spring.security.oauth2.resourceserver.jwt.issuer-uri", () ->
                keycloak.getAuthServerUrl() + "/realms/paysrv-test"
        );
    }

    protected String getAccessToken( String clientId) {
        String jsonText =
            wtc.mutate().baseUrl( keycloak.getAuthServerUrl()).build()
            .post()
            .uri("/realms/paysrv-test/protocol/openid-connect/token")
            .bodyValue(
                "grant_type=client_credentials&client_id=%s&client_secret=**********".formatted( clientId)
            )
            .header("Content-Type", "application/x-www-form-urlencoded")
            .exchange()
            .expectBody(String.class)
            .consumeWith( r -> {
                assertThat( r.getStatus())
                    .withFailMessage( "Bad status on get Keycloak token for: " + clientId)
                    .isEqualTo( HttpStatus.OK);
            })
            .returnResult()
            .getResponseBody()
        ;
        log.debug( "get access token for: {}", clientId);
        return JsonPath.parse( jsonText).read( "$.access_token").toString();
    }

    protected String getAccessToken() {
        // клиент с полными правами к сервису
        return getAccessToken( "intershop");
    }


}
