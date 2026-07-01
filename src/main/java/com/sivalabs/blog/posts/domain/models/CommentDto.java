package com.sivalabs.blog.posts.domain.models;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        String name,
        String email,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}
