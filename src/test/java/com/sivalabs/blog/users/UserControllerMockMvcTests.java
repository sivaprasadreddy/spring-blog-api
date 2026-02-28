package com.sivalabs.blog.users;

import com.sivalabs.blog.AbstractIT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;

class UserControllerMockMvcTests extends AbstractIT {
    @Test
    @DisplayName("Given valid user details, user should be created successfully")
    void shouldCreateUserSuccessfully() {
        MvcTestResult testResult = mvc.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name":"User123",
                          "email":"user123@gmail.com",
                          "password":"secret"
                        }
                        """)
                .exchange();

        assertThat(testResult)
                .hasStatus(CREATED)
                .bodyJson()
                .convertTo(UserController.RegistrationResponse.class)
                .satisfies(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.name()).isEqualTo("User123");
                    assertThat(response.email()).isEqualTo("user123@gmail.com");
                    assertThat(response.role().name()).isEqualTo("ROLE_USER");
                });
    }
}
