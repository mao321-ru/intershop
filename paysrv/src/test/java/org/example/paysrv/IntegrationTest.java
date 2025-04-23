package org.example.paysrv;

import com.jayway.jsonpath.JsonPath;
import dasniko.testcontainers.keycloak.KeycloakContainer;
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

    @Autowired
    WebTestClient wtc;

    // Using Singleton Container for all tests
    static KeycloakContainer keycloak = new KeycloakContainer( "quay.io/keycloak/keycloak:26.1.3")
            .withRealmImportFile("/dev-realm-test.json");

    // Start containers and uses Ryuk Container to remove containers when JVM process running the tests exited
    static {
        keycloak.start();
    }

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add(
            "spring.security.oauth2.resourceserver.jwt.issuer-uri", () ->
                keycloak.getAuthServerUrl() + "/realms/dev-realm"
        );
    }

    protected String getAccessToken() {
        String jsonText =
            wtc.mutate().baseUrl( keycloak.getAuthServerUrl()).build()
            .post()
            .uri("/realms/dev-realm/protocol/openid-connect/token")
            .bodyValue("grant_type=client_credentials&client_id=intershop&client_secret=test-secret")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .exchange()
            .expectBody(String.class)
            .consumeWith( r -> {
                assertThat( r.getStatus())
                    .withFailMessage( "Bad status for get auth token from Keycloak")
                    .isEqualTo( HttpStatus.OK);
            })
            .returnResult()
            .getResponseBody()
        ;
        return JsonPath.parse( jsonText).read( "$.access_token").toString();
    }

}
