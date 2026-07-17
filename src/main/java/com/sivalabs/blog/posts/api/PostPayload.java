package com.sivalabs.blog.posts.api;

import jakarta.validation.constraints.NotEmpty;

record PostPayload(
        @NotEmpty(message = "Title is required") String title,
        @NotEmpty(message = "Slug is required") String slug,
        @NotEmpty(message = "Content is required") String content,
        @NotEmpty(message = "Category is required") String categorySlug) {}
