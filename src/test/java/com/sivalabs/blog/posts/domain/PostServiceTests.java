package com.sivalabs.blog.posts.domain;

import com.sivalabs.blog.AbstractIT;
import com.sivalabs.blog.posts.domain.models.PostVO;
import com.sivalabs.blog.shared.models.PagedResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PostServiceTests extends AbstractIT {

    @Autowired
    private PostService postService;

    @Test
    void shouldFetchPostsAlongWithComments() {
        PagedResult<PostVO> posts = postService.findPostsWithComments(1);
        posts.data().forEach(System.out::println);
    }
}
