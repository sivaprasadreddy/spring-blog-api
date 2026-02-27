package com.sivalabs.blog.users;

record CreateUserCmd(String name, String email, String password, Role role) {}
