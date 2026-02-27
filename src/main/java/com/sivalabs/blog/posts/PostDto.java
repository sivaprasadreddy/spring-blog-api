package com.sivalabs.blog.posts;

import java.time.LocalDateTime;

public record PostDto(
        Long id,
        String title,
        String slug,
        String content,
        Long createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
