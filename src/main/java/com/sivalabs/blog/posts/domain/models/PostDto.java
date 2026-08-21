package com.sivalabs.blog.posts.domain.models;

import java.time.Instant;

public record PostDto(
        Long id,
        String title,
        String slug,
        String content,
        String categorySlug,
        String categoryName,
        Long authorId,
        String authorName,
        Instant createdAt,
        Instant updatedAt) {}
