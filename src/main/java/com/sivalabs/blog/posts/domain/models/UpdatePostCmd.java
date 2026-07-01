package com.sivalabs.blog.posts.domain.models;

public record UpdatePostCmd(
        Long id,
        String title,
        String slug,
        String content,
        Long userId) {}
