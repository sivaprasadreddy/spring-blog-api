package com.sivalabs.blog.posts.domain.models;

public record PostPublishedEvent(String title, String slug, String content) {}
