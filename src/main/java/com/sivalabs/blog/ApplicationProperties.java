package com.sivalabs.blog;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "blog")
@Validated
public record ApplicationProperties(
        String supportEmail,
        String emailServiceType,
        String newsletterJobCron,
        @DefaultValue("10") int pageSize) {}
