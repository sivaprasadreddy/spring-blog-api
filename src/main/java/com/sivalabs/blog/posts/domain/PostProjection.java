package com.sivalabs.blog.posts.domain;

import java.time.LocalDateTime;

interface PostProjection {
    Long getId();

    String getTitle();

    String getSlug();

    String getContent();

    Long getCreatedBy();

    String getAuthor();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();
}
