package com.sivalabs.blog.users.api;

import com.sivalabs.blog.users.domain.models.Role;

record CreateUserResponse(String name, String email, Role role) {}
