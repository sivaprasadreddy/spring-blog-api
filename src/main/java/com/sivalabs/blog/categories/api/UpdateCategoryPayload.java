package com.sivalabs.blog.categories.api;

import jakarta.validation.constraints.NotEmpty;

record UpdateCategoryPayload(
        @NotEmpty(message = "Name is required") String name,
        @NotEmpty(message = "Slug is required") String slug) {}
