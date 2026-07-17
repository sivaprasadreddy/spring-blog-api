package com.sivalabs.blog.posts.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

record CreateCommentPayload(
        @NotBlank(message = "Name is required") String name,

        @NotBlank(message = "Email is required") @Email(message = "Invalid email address") String email,

        @NotBlank(message = "Content is required") String content) {}
