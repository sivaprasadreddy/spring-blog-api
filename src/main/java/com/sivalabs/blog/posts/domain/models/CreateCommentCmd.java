package com.sivalabs.blog.posts.domain.models;

public record CreateCommentCmd(String name, String email, String content, Long postId) {}
