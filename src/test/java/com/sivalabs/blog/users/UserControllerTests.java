package com.sivalabs.blog.users;

import com.sivalabs.blog.AbstractIT;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerTests extends AbstractIT {

    @Test
    void shouldCreateUserSuccessfully() {
        UserController.RegistrationResponse response = restTestClient
                .post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "name":"User123",
                          "email":"user123@gmail.com",
                          "password":"secret"
                        }
                        """)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(UserController.RegistrationResponse.class)
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("User123");
        assertThat(response.email()).isEqualTo("user123@gmail.com");
        assertThat(response.role().name()).isEqualTo("ROLE_USER");
    }

    @Test
    void shouldReturnValidationErrorsForInvalidRegistrationRequest() {
        String response = restTestClient
                .post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "name":"",
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
        assertThat(response).contains("Name is required");
        assertThat(response).contains("Invalid email address");
        assertThat(response).contains("Password is required");
    }
}
