package com.sivalabs.blog.categories.domain.models;

import java.time.LocalDateTime;

public record CategoryDto(Long id, String name, String slug, LocalDateTime createdAt, LocalDateTime updatedAt) {}
