package com.sivalabs.blog.users.api;

import static org.springframework.http.HttpStatus.CREATED;

import com.sivalabs.blog.users.domain.UserService;
import com.sivalabs.blog.users.domain.models.CreateUserCmd;
import com.sivalabs.blog.users.domain.models.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Users API")
class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/users")
    @Operation(summary = "Create user")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created user successfully"),
    })
    ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest req) {
        LOG.info("Create user request for email: {}", req.email());
        var cmd = new CreateUserCmd(req.name(), req.email(), req.password(), Role.ROLE_USER);
        userService.createUser(cmd);
        var response = new CreateUserResponse(req.name(), req.email(), Role.ROLE_USER);
        return ResponseEntity.status(CREATED.value()).body(response);
    }
}
