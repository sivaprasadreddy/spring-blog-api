package com.sivalabs.blog.categories.api;

import jakarta.validation.constraints.NotBlank;

record CreateCategoryPayload(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Slug is required") String slug) {}
