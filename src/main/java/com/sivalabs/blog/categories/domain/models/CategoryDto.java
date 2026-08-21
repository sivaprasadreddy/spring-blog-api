package com.sivalabs.blog.categories.domain.models;

import java.time.Instant;

public record CategoryDto(Long id, String name, String slug, Instant createdAt, Instant updatedAt) {}
