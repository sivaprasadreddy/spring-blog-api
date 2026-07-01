package com.sivalabs.blog.posts.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

record CreateCommentPayload(
        @NotEmpty(message = "Name is required") String name,

        @NotEmpty(message = "Email is required") @Email(message = "Invalid email address") String email,

        @NotEmpty(message = "Content is required") String content) {}
