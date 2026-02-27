package com.sivalabs.blog.auth;

import java.time.Instant;

record LoginResult(
        String accessToken,
        Instant expiresAt,
        Long userId,
        String name,
        String email,
        String role) {}
