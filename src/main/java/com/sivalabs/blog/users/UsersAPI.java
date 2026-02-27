package com.sivalabs.blog.users;

import com.sivalabs.blog.shared.PagedResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersAPI {
    private final UserService userService;
    private final UserMapper userMapper;

    UsersAPI(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    public Optional<UserDto> findById(Long id) {
        return userService.findById(id).map(userMapper::toUserDto);
    }

    public Optional<UserDto> findByEmailWithPassword(String email) {
        return userService.findByEmail(email).map(userMapper::toUserDtoWithPassword);
    }

    public Optional<UserDto> findByEmail(String email) {
        return userService.findByEmail(email).map(userMapper::toUserDto);
    }

    public List<UserDto> findAllUsers() {
        return userService.findAllUsers().stream().map(userMapper::toUserDto).toList();
    }
}
