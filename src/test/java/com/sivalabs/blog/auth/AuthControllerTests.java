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
}
