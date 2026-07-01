package com.sivalabs.blog.users.domain.models;

public record CreateUserCmd(String name, String email, String password, Role role) {}
