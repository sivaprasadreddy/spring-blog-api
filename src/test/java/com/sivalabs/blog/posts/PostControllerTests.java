package com.sivalabs.blog.posts;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.blog.AbstractIT;
import com.sivalabs.blog.shared.PagedResult;
import com.sivalabs.blog.users.Role;
import com.sivalabs.blog.users.UserDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

@Sql("/test-data.sql")
class PostControllerTests extends AbstractIT {

    @Test
    void shouldGetPosts() {
        PagedResult<PostDto> result = restTestClient.get()
                .uri("/api/posts")
                .exchange()
                .expectStatus().isOk()
                .returnResult(new ParameterizedTypeReference<PagedResult<PostDto>>() {
                })
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
        PagedResult<PostDto> result = restTestClient.get()
                .uri("/api/posts?query=spring")
                .exchange()
                .expectStatus().isOk()
                .returnResult(new ParameterizedTypeReference<PagedResult<PostDto>>() {
                })
                .getResponseBody();

        assertThat(result).isNotNull();
        assertThat(result.data()).hasSize(4);

    }

    @Test
    void shouldGetPostBySlug() {
        PostDto postDto = restTestClient.get()
                .uri("/api/posts/{slug}", "introducing-springboot")
                .exchange()
                .expectStatus().isOk()
                .returnResult(PostDto.class)
                .getResponseBody();

        assertThat(postDto).isNotNull();
        assertThat(postDto.id()).isEqualTo(2);
        assertThat(postDto.title()).isEqualTo("SpringBoot: Introducing SpringBoot");
        assertThat(postDto.slug()).isEqualTo("introducing-springboot");
    }

    @Test
    void shouldGetPostComments() {
        List<CommentDto> commentDtos = restTestClient.get()
                .uri("/api/posts/{slug}/comments", "introducing-springboot")
                .exchange()
                .expectStatus().isOk()
                .returnResult(new ParameterizedTypeReference<List<CommentDto>>() {
                })
                .getResponseBody();

        assertThat(commentDtos).hasSize(2);
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
                          "content":"Post content"
                        }
                        """)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    void shouldUpdatePostSuccessfully() {
        var payload = """
            {
              "title":"Installing LinuxMint OS",
              "slug":"installing-linuxmint-os",
              "content":"Installing LinuxMint 22"
            }
            """;
        UserDto userDto = new UserDto(2L, "Siva", "siva@gmail.com", "", Role.ROLE_USER);
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
}
