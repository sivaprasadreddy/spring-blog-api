package com.sivalabs.blog.posts;

record UpdatePostCmd(
        Long id,
        String title,
        String slug,
        String content,
        Long userId) {}
