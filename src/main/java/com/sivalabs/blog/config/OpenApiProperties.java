package com.sivalabs.blog.config;

import jakarta.validation.Valid;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "blog.openapi")
@Validated
record OpenApiProperties(
        @DefaultValue("Blog API") String title,

        @DefaultValue("Blog API Swagger Documentation") String description,

        @DefaultValue("v1.0.0") String version,
        @Valid OpenApiProperties.Contact contact) {

    public record Contact(
            @DefaultValue("SivaLabs") String name,
            @DefaultValue("support@sivalabs.in") String email) {}
}
