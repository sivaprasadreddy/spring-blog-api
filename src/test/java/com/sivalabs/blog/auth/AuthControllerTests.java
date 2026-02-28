package com.sivalabs.blog.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.blog.AbstractIT;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class AuthControllerTests extends AbstractIT {

    @Test
    void shouldLoginSuccessfully() {
        AuthController.LoginResponse response = restTestClient
                .post()
                .uri("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "email":"siva@gmail.com",
                            "password":"siva"
                        }
                        """)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(AuthController.LoginResponse.class)
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Siva Prasad");
        assertThat(response.email()).isEqualTo("siva@gmail.com");
        assertThat(response.token()).isNotBlank();
    }

    @Test
    void shouldReturnUnauthorizedForInvalidCredentials() {
        String response = restTestClient
                .post()
                .uri("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "email":"siva@gmail.com",
                          "password":"invalid-password"
                        }
                        """)
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .returnResult(String.class)
                .getResponseBody();

        assertThat(response).contains("Unauthorized");
    }

    @Test
    void shouldReturnValidationErrorsForInvalidLoginRequest() {
        String response = restTestClient
                .post()
                .uri("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "email":"invalid-email",
                          "password":""
                        }
                        """)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .returnResult(String.class)
                .getResponseBody();

        assertThat(response).contains("Validation Error");
        assertThat(response).contains("Invalid email address");
        assertThat(response).contains("Password is required");
    }
}
