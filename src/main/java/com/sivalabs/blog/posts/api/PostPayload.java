package com.sivalabs.blog.posts.api;

import jakarta.validation.constraints.NotBlank;

record PostPayload(
        @NotBlank(message = "Title is required") String title,
        @NotBlank(message = "Slug is required") String slug,
        @NotBlank(message = "Content is required") String content,
        @NotBlank(message = "Category is required") String categorySlug) {}
