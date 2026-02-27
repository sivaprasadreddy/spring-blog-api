package com.sivalabs.blog.posts;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostsAPI {
    private final PostService postService;

    PostsAPI(PostService postService) {
        this.postService = postService;
    }

    public List<PostDto> findPostsCreatedBetween(LocalDateTime start, LocalDateTime end) {
        return postService.findPostsCreatedBetween(start, end)
                .stream()
                .toList();
    }
}
