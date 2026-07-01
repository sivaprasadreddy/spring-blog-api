package com.sivalabs.blog.users;

import com.sivalabs.blog.users.domain.UserService;
import com.sivalabs.blog.users.domain.models.UserDto;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UsersAPI {
    private final UserService userService;

    UsersAPI(UserService userService) {
        this.userService = userService;
    }

    public Optional<UserDto> findById(Long id) {
        return userService.findById(id);
    }

    public Optional<UserDto> findByEmailWithPassword(String email) {
        return userService.findByEmail(email);
    }

    public Optional<UserDto> findByEmail(String email) {
        return userService.findByEmail(email);
    }

    public List<UserDto> findAllUsers() {
        return userService.findAllUsers();
    }
}
