package com.sivalabs.blog.posts.domain.models;

import java.time.Instant;

public record CommentDto(Long id, String name, String email, String content, Instant createdAt, Instant updatedAt) {}
