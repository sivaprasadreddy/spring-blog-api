package com.sivalabs.blog.posts.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.blog.AbstractIT;
import com.sivalabs.blog.posts.domain.models.CommentDto;
import com.sivalabs.blog.posts.domain.models.PostDto;
import com.sivalabs.blog.shared.models.PagedResult;
import com.sivalabs.blog.users.domain.models.Role;
import com.sivalabs.blog.users.domain.models.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

class PostControllerTests extends AbstractIT {

    @Test
    void shouldGetPosts() {
        PagedResult<PostDto> result = restTestClient
                .get()
                .uri("/api/posts")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(new ParameterizedTypeReference<PagedResult<PostDto>>() {})
                .getResponseBody();

        assertThat(result).isNotNull();
        assertThat(result.data()).hasSize(5);
        assertThat(result.currentPageNo()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(2);
        assertThat(result.totalElements()).isEqualTo(9);
        assertThat(result.hasNextPage()).isTrue();
        assertThat(result.hasPreviousPage()).isFalse();
    }

    @Test
    void shouldSearchPosts() {
        PagedResult<PostDto> result = restTestClient
                .get()
                .uri("/api/posts?query=spring")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(new ParameterizedTypeReference<PagedResult<PostDto>>() {})
                .getResponseBody();

        assertThat(result).isNotNull();
        assertThat(result.data()).hasSize(4);
    }

    @Test
    void shouldGetPostBySlug() {
        PostDto postDto = restTestClient
                .get()
                .uri("/api/posts/{slug}", "introducing-springboot")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(PostDto.class)
                .getResponseBody();

        assertThat(postDto).isNotNull();
        assertThat(postDto.id()).isEqualTo(2);
        assertThat(postDto.title()).isEqualTo("SpringBoot: Introducing SpringBoot");
        assertThat(postDto.slug()).isEqualTo("introducing-springboot");
        assertThat(postDto.categorySlug()).isEqualTo("spring");
        assertThat(postDto.categoryName()).isEqualTo("Spring");
    }

    @Test
    void shouldReturnNotFoundWhenPostSlugDoesNotExist() {
        String response = restTestClient
                .get()
                .uri("/api/posts/{slug}", "missing-post-slug")
                .exchange()
                .expectStatus()
                .isNotFound()
                .returnResult(String.class)
                .getResponseBody();

        assertThat(response).contains("Resource Not Found");
        assertThat(response).contains("Post with slug 'missing-post-slug' not found");
    }

    @Test
    void shouldGetPostComments() {
        PagedResult<CommentDto> result = restTestClient
                .get()
                .uri("/api/posts/{slug}/comments", "introducing-springboot")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(new ParameterizedTypeReference<PagedResult<CommentDto>>() {})
                .getResponseBody();

        assertThat(result).isNotNull();
        assertThat(result.data()).hasSize(2);
    }

    @Test
    void shouldReturnNotFoundWhenGettingCommentsForUnknownPost() {
        String response = restTestClient
                .get()
                .uri("/api/posts/{slug}/comments", "missing-post-slug")
                .exchange()
                .expectStatus()
                .isNotFound()
                .returnResult(String.class)
                .getResponseBody();

        assertThat(response).contains("Resource Not Found");
        assertThat(response).contains("Post with slug 'missing-post-slug' not found");
    }

    @Test
    void shouldCreateCommentSuccessfully() {
        restTestClient
                .post()
                .uri("/api/posts/{slug}/comments", "introducing-springboot")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "name": "Siva",
                            "email": "siva@gmail.com",
                            "content": "Test comment"
                        }
                        """)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    void shouldReturnValidationErrorsForInvalidCommentPayload() {
        String response = restTestClient
                .post()
                .uri("/api/posts/{slug}/comments", "introducing-springboot")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "name": "",
                          "email": "invalid-email",
                          "content": ""
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
        assertThat(response).contains("Content is required");
    }

    @Test
    void shouldReturnNotFoundWhenCreatingCommentForUnknownPost() {
        String response = restTestClient
                .post()
                .uri("/api/posts/{slug}/comments", "missing-post-slug")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "name": "Siva",
                          "email": "siva@gmail.com",
                          "content": "Test comment"
                        }
                        """)
                .exchange()
                .expectStatus()
                .isNotFound()
                .returnResult(String.class)
                .getResponseBody();

        assertThat(response).contains("Resource Not Found");
        assertThat(response).contains("Post with slug 'missing-post-slug' not found");
    }

    @Test
    void shouldCreatePostSuccessfully() {
        UserDto userDto = new UserDto(2L, "Siva", "siva@gmail.com", "", Role.ROLE_USER);
        String token = this.createToken(userDto);

        restTestClient
                .post()
                .uri("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body("""
                        {
                          "title":"Post Title",
                          "slug":"post-slug",
                          "content":"Post content",
                          "categorySlug":"spring"
                        }
                        """)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    void shouldCreatePostWithDefaultCategoryWhenCategorySlugIsNotProvided() {
        UserDto userDto = new UserDto(2L, "Siva", "siva@gmail.com", "", Role.ROLE_USER);
        String token = this.createToken(userDto);

        restTestClient
                .post()
                .uri("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body("""
                        {
                          "title":"Post Without Category",
                          "slug":"post-without-category",
                          "content":"Post content"
                        }
                        """)
                .exchange()
                .expectStatus()
                .isCreated();

        PostDto postDto = restTestClient
                .get()
                .uri("/api/posts/{slug}", "post-without-category")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(PostDto.class)
                .getResponseBody();

        assertThat(postDto).isNotNull();
        assertThat(postDto.categorySlug()).isEqualTo("general");
        assertThat(postDto.categoryName()).isEqualTo("General");
    }

    @Test
    void shouldReturnUnauthorizedWhenCreatingPostWithoutToken() {
        restTestClient
                .post()
                .uri("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "title":"Post Title",
                          "slug":"post-slug",
                          "content":"Post content",
                          "categorySlug":"spring"
                        }
                        """)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void shouldReturnValidationErrorsWhenCreatingPostWithInvalidPayload() {
        UserDto userDto = new UserDto(2L, "Siva", "siva@gmail.com", "", Role.ROLE_USER);
        String token = this.createToken(userDto);

        String response = restTestClient
                .post()
                .uri("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body("""
                        {
                          "title":"",
                          "slug":"",
                          "content":""
                        }
                        """)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .returnResult(String.class)
                .getResponseBody();

        assertThat(response).contains("Validation Error");
        assertThat(response).contains("Title is required");
        assertThat(response).contains("Slug is required");
        assertThat(response).contains("Content is required");
    }

    @Test
    void shouldReturnUnprocessableContentWhenCreatingPostWithDuplicateSlug() {
        UserDto userDto = new UserDto(2L, "Siva", "siva@gmail.com", "", Role.ROLE_USER);
        String token = this.createToken(userDto);

        String response = restTestClient
                .post()
                .uri("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body("""
                        {
                          "title":"Duplicate Slug Post",
                          "slug":"introducing-springboot",
                          "content":"Post content",
                          "categorySlug":"spring"
                        }
                        """)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .returnResult(String.class)
                .getResponseBody();

        assertThat(response).contains("Bad Request");
        assertThat(response).contains("Post with slug introducing-springboot already exists");
    }

    @Test
    void shouldUpdatePostSuccessfully() {
        var payload = """
            {
              "title":"Installing LinuxMint OS",
              "slug":"installing-linuxmint-os",
              "content":"Installing LinuxMint 22",
              "categorySlug":"spring"
            }
            """;
        UserDto userDto = new UserDto(1L, "Administrator", "admin@gmail.com", "", Role.ROLE_ADMIN);
        String token = this.createToken(userDto);

        restTestClient
                .put()
                .uri("/api/posts/{slug}", "installing-linuxmint")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(payload)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void shouldUpdatePostKeepingSameSlug() {
        var payload = """
            {
              "title":"Installing LinuxMint OS",
              "slug":"installing-linuxmint",
              "content":"Installing LinuxMint 22",
              "categorySlug":"spring"
            }
            """;
        UserDto userDto = new UserDto(1L, "Administrator", "admin@gmail.com", "", Role.ROLE_ADMIN);
        String token = this.createToken(userDto);

        restTestClient
                .put()
                .uri("/api/posts/{slug}", "installing-linuxmint")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(payload)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void shouldUpdatePostWithDefaultCategoryWhenCategorySlugIsNotProvided() {
        var payload = """
            {
              "title":"Installing LinuxMint OS",
              "slug":"installing-linuxmint",
              "content":"Installing LinuxMint 22"
            }
            """;
        UserDto userDto = new UserDto(1L, "Administrator", "admin@gmail.com", "", Role.ROLE_ADMIN);
        String token = this.createToken(userDto);

        restTestClient
                .put()
                .uri("/api/posts/{slug}", "installing-linuxmint")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(payload)
                .exchange()
                .expectStatus()
                .isOk();

        PostDto postDto = restTestClient
                .get()
                .uri("/api/posts/{slug}", "installing-linuxmint")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(PostDto.class)
                .getResponseBody();

        assertThat(postDto).isNotNull();
        assertThat(postDto.categorySlug()).isEqualTo("general");
        assertThat(postDto.categoryName()).isEqualTo("General");
    }

    @Test
    void shouldReturnForbiddenWhenUpdatingPostOwnedByAnotherUser() {
        var payload = """
            {
              "title":"Installing LinuxMint OS",
              "slug":"installing-linuxmint-os",
              "content":"Installing LinuxMint 22",
              "categorySlug":"spring"
            }
            """;
        UserDto userDto = new UserDto(2L, "Siva", "siva@gmail.com", "", Role.ROLE_USER);
        String token = this.createToken(userDto);

        String response = restTestClient
                .put()
                .uri("/api/posts/{slug}", "installing-linuxmint")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(payload)
                .exchange()
                .expectStatus()
                .isForbidden()
                .returnResult(String.class)
                .getResponseBody();

        assertThat(response).contains("Access Denied");
        assertThat(response).contains("Only the post owner can modify the post");
    }

    @Test
    void shouldReturnUnauthorizedWhenUpdatingPostWithoutToken() {
        var payload = """
            {
              "title":"Installing LinuxMint OS",
              "slug":"installing-linuxmint-os",
              "content":"Installing LinuxMint 22",
              "categorySlug":"spring"
            }
            """;

        restTestClient
                .put()
                .uri("/api/posts/{slug}", "installing-linuxmint")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownPost() {
        var payload = """
            {
              "title":"Unknown Post",
              "slug":"unknown-post",
              "content":"Updated content",
              "categorySlug":"spring"
            }
            """;
        UserDto userDto = new UserDto(2L, "Siva", "siva@gmail.com", "", Role.ROLE_USER);
        String token = this.createToken(userDto);

        String response = restTestClient
                .put()
                .uri("/api/posts/{slug}", "missing-post-slug")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(payload)
                .exchange()
                .expectStatus()
                .isNotFound()
                .returnResult(String.class)
                .getResponseBody();

        assertThat(response).contains("Resource Not Found");
        assertThat(response).contains("Post with slug 'missing-post-slug' not found");
    }

    @Test
    void shouldReturnValidationErrorsWhenUpdatingPostWithInvalidPayload() {
        var payload = """
            {
              "title":"",
              "slug":"",
              "content":""
            }
            """;
        UserDto userDto = new UserDto(2L, "Siva", "siva@gmail.com", "", Role.ROLE_USER);
        String token = this.createToken(userDto);

        String response = restTestClient
                .put()
                .uri("/api/posts/{slug}", "installing-linuxmint")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(payload)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .returnResult(String.class)
                .getResponseBody();

        assertThat(response).contains("Validation Error");
        assertThat(response).contains("Title is required");
        assertThat(response).contains("Slug is required");
        assertThat(response).contains("Content is required");
    }

    @Test
    void shouldReturnUnprocessableContentWhenUpdatingPostWithDuplicateSlug() {
        var payload = """
            {
              "title":"Installing LinuxMint OS",
              "slug":"introducing-springboot",
              "content":"Installing LinuxMint 22",
              "categorySlug":"spring"
            }
            """;
        UserDto userDto = new UserDto(1L, "Administrator", "admin@gmail.com", "", Role.ROLE_ADMIN);
        String token = this.createToken(userDto);

        String response = restTestClient
                .put()
                .uri("/api/posts/{slug}", "installing-linuxmint")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(payload)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .returnResult(String.class)
                .getResponseBody();

        assertThat(response).contains("Bad Request");
        assertThat(response).contains("Post with slug introducing-springboot already exists");
    }
}
