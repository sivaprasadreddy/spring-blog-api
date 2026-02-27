package com.sivalabs.blog.posts;

import java.time.LocalDateTime;

interface PostProjection {
    Long getId();
    String getTitle();
    String getSlug();
    String getContent();
    Long getCreatedBy();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
}


