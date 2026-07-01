package com.sivalabs.blog.users.domain;

import com.sivalabs.blog.users.domain.models.CreateUserCmd;
import com.sivalabs.blog.users.domain.models.UserDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Optional<UserDto> findById(Long id) {
        return userRepository.findById(id).map(userMapper::toUserDto);
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserDto).toList();
    }

    @Transactional(readOnly = true)
    public Optional<UserDto> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).map(userMapper::toUserDtoWithPassword);
    }

    @Transactional
    public void createUser(CreateUserCmd cmd) {
        var user = new User();
        user.setName(cmd.name());
        user.setEmail(cmd.email());
        user.setPassword(passwordEncoder.encode(cmd.password()));
        user.setRole(cmd.role());
        userRepository.save(user);
    }
}
