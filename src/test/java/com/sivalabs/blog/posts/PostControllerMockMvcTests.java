package com.sivalabs.blog.posts;

import com.sivalabs.blog.AbstractIT;
import com.sivalabs.blog.shared.PagedResult;
import com.sivalabs.blog.users.Role;
import com.sivalabs.blog.users.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PostControllerMockMvcTests extends AbstractIT {
    @Test
    void shouldGetPosts() {
        mvc.get()
                .uri("/api/posts")
                .exchange()
                .assertThat()
                .hasStatusOk()
                .bodyJson()
                .convertTo(PagedResult.class)
                .satisfies(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.data()).hasSize(5);
                    assertThat(result.currentPageNo()).isEqualTo(1);
                    assertThat(result.totalPages()).isEqualTo(2);
                    assertThat(result.totalElements()).isEqualTo(9);
                    assertThat(result.hasNextPage()).isTrue();
                    assertThat(result.hasPreviousPage()).isFalse();
                });
    }

    @Test
    void shouldSearchPosts() {
        mvc.get()
                .uri("/api/posts?query=spring")
                .exchange()
                .assertThat()
                .hasStatusOk()
                .bodyJson()
                .convertTo(PagedResult.class)
                .satisfies(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.data()).hasSize(4);
                });
    }

    @Test
    void shouldGetPostBySlug() {
        mvc.get()
                .uri("/api/posts/{slug}", "introducing-springboot")
                .exchange()
                .assertThat()
                .hasStatusOk()
                .bodyJson()
                .convertTo(PostDto.class)
                .satisfies(postDto -> {
                    assertThat(postDto).isNotNull();
                    assertThat(postDto.id()).isEqualTo(2);
                    assertThat(postDto.title()).isEqualTo("SpringBoot: Introducing SpringBoot");
                    assertThat(postDto.slug()).isEqualTo("introducing-springboot");
                });
    }

    @Test
    void shouldGetPostComments() {
        mvc.get()
                .uri("/api/posts/{slug}/comments", "introducing-springboot")
                .exchange()
                .assertThat()
                .hasStatusOk()
                .bodyJson()
                .convertTo(List.class)
                .satisfies(commentDtos -> {
                    assertThat(commentDtos).hasSize(2);
                });
    }

    @Test
    void shouldCreateCommentSuccessfully() {
        mvc.post()
                .uri("/api/posts/{slug}/comments", "introducing-springboot")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Siva",
                            "email": "siva@gmail.com",
                            "content": "Test comment"
                        }
                        """)
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.CREATED);
    }

    @Test
    void shouldCreatePostSuccessfully() {
        UserDto userDto = new UserDto(2L, "Siva", "siva@gmail.com", "", Role.ROLE_USER);
        String token = this.createToken(userDto);

        mvc.post()
                .uri("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content("""
                        {
                          "title":"Post Title",
                          "slug":"post-slug",
                          "content":"Post content"
                        }
                        """)
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.CREATED)
                .redirectedUrl()
                .endsWith("/api/posts/post-slug");
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

        mvc.put()
                .uri("/api/posts/{slug}", "installing-linuxmint")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(payload)
                .exchange()
                .assertThat()
                .hasStatusOk()
                .redirectedUrl()
                .endsWith("/api/posts/installing-linuxmint-os");
    }
}
