package com.sivalabs.blog.posts.domain.models;

public record UpdatePostCmd(
        String slug, String newTitle, String newSlug, String newContent, String categorySlug, Long userId) {}
