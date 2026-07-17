package com.sivalabs.blog.categories.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.blog.AbstractIT;
import com.sivalabs.blog.categories.domain.models.CategoryDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

class CategoryControllerTests extends AbstractIT {

    @Test
    void shouldGetCategories() {
        List<CategoryDto> result = restTestClient
                .get()
                .uri("/api/categories")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(new ParameterizedTypeReference<List<CategoryDto>>() {})
                .getResponseBody();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).extracting(CategoryDto::slug).contains("java", "spring", "general");
    }

    @Test
    void shouldGetCategoryBySlug() {
        CategoryDto category = restTestClient
                .get()
                .uri("/api/categories/{slug}", "spring")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CategoryDto.class)
                .getResponseBody();

        assertThat(category).isNotNull();
        assertThat(category.slug()).isEqualTo("spring");
        assertThat(category.name()).isEqualTo("Spring");
    }

    @Test
    void shouldReturnNotFoundWhenCategorySlugDoesNotExist() {
        String response = restTestClient
                .get()
                .uri("/api/categories/{slug}", "missing-category-slug")
                .exchange()
                .expectStatus()
                .isNotFound()
                .returnResult(String.class)
                .getResponseBody();

        assertThat(response).contains("Resource Not Found");
        assertThat(response).contains("Category with slug 'missing-category-slug' not found");
    }

    @Test
    void shouldCreateCategorySuccessfully() {
        restTestClient
                .post()
                .uri("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken())
                .body("""
                        {
                          "name":"DevOps",
                          "slug":"devops"
                        }
                        """)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    void shouldReturnUnauthorizedWhenCreatingCategoryWithoutToken() {
        restTestClient
                .post()
                .uri("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "name":"DevOps",
                          "slug":"devops"
                        }
                        """)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void shouldReturnValidationErrorsWhenCreatingCategoryWithInvalidPayload() {
        String response = restTestClient
                .post()
                .uri("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken())
                .body("""
                        {
                          "name":"",
                          "slug":""
                        }
                        """)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .returnResult(String.class)
                .getResponseBody();

        assertThat(response).contains("Validation Error");
        assertThat(response).contains("Name is required");
        assertThat(response).contains("Slug is required");
    }

    @Test
    void shouldReturnBadRequestWhenCreatingCategoryWithDuplicateSlug() {
        String response = restTestClient
                .post()
                .uri("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken())
                .body("""
                        {
                          "name":"Spring Boot",
                          "slug":"spring"
                        }
                        """)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .returnResult(String.class)
                .getResponseBody();

        assertThat(response).contains("Bad Request");
        assertThat(response).contains("Category with slug spring already exists");
    }

    @Test
    void shouldUpdateCategorySuccessfully() {
        restTestClient
                .put()
                .uri("/api/categories/{slug}", "java")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken())
                .body("""
                        {
                          "name":"Java Programming",
                          "slug":"java-programming"
                        }
                        """)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownCategory() {
        String response = restTestClient
                .put()
                .uri("/api/categories/{slug}", "missing-category-slug")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken())
                .body("""
                        {
                          "name":"Unknown",
                          "slug":"unknown"
                        }
                        """)
                .exchange()
                .expectStatus()
                .isNotFound()
                .returnResult(String.class)
                .getResponseBody();

        assertThat(response).contains("Resource Not Found");
        assertThat(response).contains("Category with slug 'missing-category-slug' not found");
    }

    @Test
    void shouldDeleteCategorySuccessfully() {
        // Create a category that has no posts referencing it, then delete it
        restTestClient
                .post()
                .uri("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken())
                .body("""
                        {
                          "name":"Temporary",
                          "slug":"temporary"
                        }
                        """)
                .exchange()
                .expectStatus()
                .isCreated();

        restTestClient
                .delete()
                .uri("/api/categories/{slug}", "temporary")
                .header("Authorization", "Bearer " + adminToken())
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void shouldReturnUnauthorizedWhenDeletingCategoryWithoutToken() {
        restTestClient
                .delete()
                .uri("/api/categories/{slug}", "general")
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void shouldReturnNotFoundWhenDeletingUnknownCategory() {
        restTestClient
                .delete()
                .uri("/api/categories/{slug}", "missing-category-slug")
                .header("Authorization", "Bearer " + adminToken())
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void shouldReturnBadRequestWhenDeletingCategoryAssociatedWithPosts() {
        String response = restTestClient
                .delete()
                .uri("/api/categories/{slug}", "spring")
                .header("Authorization", "Bearer " + adminToken())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .returnResult(String.class)
                .getResponseBody();

        assertThat(response).contains("Bad Request");
        assertThat(response).contains("Category cannot be deleted because it is associated with one or more posts");
    }
}
