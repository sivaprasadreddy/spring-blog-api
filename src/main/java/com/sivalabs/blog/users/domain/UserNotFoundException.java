package com.sivalabs.blog.users.domain;

import com.sivalabs.blog.shared.exceptions.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
