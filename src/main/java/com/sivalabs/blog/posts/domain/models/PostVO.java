package com.sivalabs.blog.posts.domain.models;

import java.time.Instant;
import java.util.List;

public record PostVO(
        Long id, String title, String slug, String content, Instant createdAt, List<CommentDto> comments) {}
