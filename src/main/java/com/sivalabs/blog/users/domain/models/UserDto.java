package com.sivalabs.blog.users.domain.models;

public record UserDto(Long id, String name, String email, String password, Role role) {}
