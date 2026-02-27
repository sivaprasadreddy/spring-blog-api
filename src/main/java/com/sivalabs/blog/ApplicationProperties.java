package com.sivalabs.blog;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "blog")
@Validated
public record ApplicationProperties(
        String supportEmail,
        String newsletterJobCron,
        @DefaultValue("10") int postsPerPage,
        @Valid JwtProperties jwt,
        @Valid CorsProperties cors,
        @Valid OpenAPIProperties openApi) {
    public record JwtProperties(
            @DefaultValue("BootifulBlog") String issuer,
            @DefaultValue("604800") Long expiresInSeconds,
            @NotNull RSAPublicKey publicKey,
            @NotNull RSAPrivateKey privateKey) {}

    public record CorsProperties(
            @DefaultValue("/api/**") String pathPattern,
            @DefaultValue("*") String allowedOrigins,
            @DefaultValue("*") String allowedMethods,
            @DefaultValue("*") String allowedHeaders) {}

    public record OpenAPIProperties(
            @DefaultValue("Bootiful Blog API") String title,

            @DefaultValue("Bootiful Blog API Swagger Documentation")
            String description,

            @DefaultValue("v1.0.0") String version,
            @Valid OpenAPIProperties.Contact contact) {

        public record Contact(
                @DefaultValue("SivaLabs") String name,
                @DefaultValue("support@sivalabs.in") String email) {}
    }
}
