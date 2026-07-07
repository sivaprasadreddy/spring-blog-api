package com.sivalabs.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class WebMvcConfig implements WebMvcConfigurer {
    private final CorsProperties corsProperties;

    WebMvcConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(corsProperties.pathPattern())
                .allowedOriginPatterns(corsProperties.allowedOrigins())
                .allowedMethods(corsProperties.allowedMethods())
                .allowedHeaders(corsProperties.allowedHeaders());
    }
}
