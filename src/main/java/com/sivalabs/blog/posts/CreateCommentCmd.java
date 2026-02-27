package com.sivalabs.blog.posts;

record CreateCommentCmd(
        String name,
        String email,
        String content,
        Long postId) {}
