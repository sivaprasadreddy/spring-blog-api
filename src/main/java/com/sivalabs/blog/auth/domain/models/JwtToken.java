package com.sivalabs.blog.auth.domain.models;

import java.time.Instant;

public record JwtToken(String token, Instant expiresAt) {}
