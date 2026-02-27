package com.sivalabs.blog;

import org.springframework.boot.SpringApplication;

public class TestBlogApplication {

    public static void main(String[] args) {
        System.setProperty("spring.docker.compose.enabled", "false");
        SpringApplication.from(BlogApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
