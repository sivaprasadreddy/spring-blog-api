package com.sivalabs.blog.auth.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

record LoginRequest(
        @NotEmpty(message = "Email is required") @Email(message = "Invalid email address") String email,

        @NotEmpty(message = "Password is required") String password) {}
