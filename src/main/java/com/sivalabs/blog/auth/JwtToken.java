package com.sivalabs.blog.auth;

import java.time.Instant;

public record JwtToken(String token, Instant expiresAt) {}
