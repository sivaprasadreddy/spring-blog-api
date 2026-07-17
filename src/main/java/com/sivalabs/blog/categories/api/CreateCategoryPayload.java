package com.sivalabs.blog.categories.api;

import jakarta.validation.constraints.NotEmpty;

record CreateCategoryPayload(
        @NotEmpty(message = "Name is required") String name,
        @NotEmpty(message = "Slug is required") String slug) {}
