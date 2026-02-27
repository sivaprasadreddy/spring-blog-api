package com.sivalabs.blog.users;

public record UserDto(Long id, String name, String email, String password, Role role) {}
