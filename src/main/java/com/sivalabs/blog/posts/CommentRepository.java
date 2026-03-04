package com.sivalabs.blog.posts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
}
