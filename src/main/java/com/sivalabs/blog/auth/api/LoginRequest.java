package com.sivalabs.blog.auth.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

record LoginRequest(
        @NotBlank(message = "Email is required") @Email(message = "Invalid email address") String email,

        @NotBlank(message = "Password is required") String password) {}
