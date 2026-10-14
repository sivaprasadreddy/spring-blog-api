package com.sivalabs.blog.users.api;

import static org.springframework.http.HttpStatus.CREATED;

import com.sivalabs.blog.users.domain.UserNotFoundException;
import com.sivalabs.blog.users.domain.UserService;
import com.sivalabs.blog.users.domain.models.CreateUserCmd;
import com.sivalabs.blog.users.domain.models.Role;
import com.sivalabs.blog.users.domain.models.UserDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/users")
@Tag(name = "Users API")
class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest req) {
        LOG.info("Create user request for email: {}", req.email());
        var cmd = new CreateUserCmd(req.name(), req.email(), req.password(), Role.ROLE_USER);
        userService.createUser(cmd);
        var response = new CreateUserResponse(req.name(), req.email(), Role.ROLE_USER);
        return ResponseEntity.status(CREATED.value()).body(response);
    }

    @GetMapping(value = "/{id}")
    ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        var user = userService
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id %d not found".formatted(id)));
        return ResponseEntity.ok(user);
    }
}
