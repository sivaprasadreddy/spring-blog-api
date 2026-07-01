package com.sivalabs.blog.posts.domain.models;

public record CreatePostCmd(
        String title,
        String slug,
        String content,
        Long createdBy) {}
