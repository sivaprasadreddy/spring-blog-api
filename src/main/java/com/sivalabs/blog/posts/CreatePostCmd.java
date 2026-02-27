package com.sivalabs.blog.posts;

record CreatePostCmd(
        String title,
        String slug,
        String content,
        Long createdBy) {}
