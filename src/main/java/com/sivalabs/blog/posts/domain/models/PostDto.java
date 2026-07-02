package com.sivalabs.blog.posts.domain.models;

import java.time.LocalDateTime;

public record PostDto(
        Long id,
        String title,
        String slug,
        String content,
        Long authorId,
        String authorName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}
