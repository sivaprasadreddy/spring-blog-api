package com.sivalabs.blog.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

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
    ResponseEntity<RegistrationResponse> createUser(@RequestBody @Valid RegistrationRequest req) {
        LOG.info("Registration request for email: {}", req.email());
        var cmd = new CreateUserCmd(req.name(), req.email(), req.password(), Role.ROLE_USER);
        userService.createUser(cmd);
        var response = new RegistrationResponse(req.name(), req.email(), Role.ROLE_USER);
        return ResponseEntity.status(CREATED.value()).body(response);
    }

    public record RegistrationRequest(
            @NotBlank(message = "Name is required") String name,

            @NotBlank(message = "Email is required") @Email(message = "Invalid email address") String email,

            @NotBlank(message = "Password is required") String password) {}

    public record RegistrationResponse(String name, String email, Role role) {}
}
