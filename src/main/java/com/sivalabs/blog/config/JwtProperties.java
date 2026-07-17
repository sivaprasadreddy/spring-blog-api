package com.sivalabs.blog.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "blog.jwt")
@Validated
public record JwtProperties(
        @NotBlank String issuer,
        @NotNull Long expiresInSeconds,
        @NotNull RSAPublicKey publicKey,
        @NotNull RSAPrivateKey privateKey) {}
