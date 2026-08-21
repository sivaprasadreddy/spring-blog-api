package com.sivalabs.blog.posts;

import com.sivalabs.blog.posts.domain.PostService;
import com.sivalabs.blog.posts.domain.models.PostDto;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PostsAPI {
    private final PostService postService;

    PostsAPI(PostService postService) {
        this.postService = postService;
    }

    public List<PostDto> findPostsCreatedBetween(Instant start, Instant end) {
        return postService.findPostsCreatedBetween(start, end);
    }
}
