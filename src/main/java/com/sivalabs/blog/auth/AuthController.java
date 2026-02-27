package com.sivalabs.blog.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@Tag(name = "Auth API")
class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/api/login")
    @Operation(summary = "Authenticate user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Returns successful authentication response"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
    })
    LoginResponse login(@RequestBody @Valid LoginRequest req) {
        log.info("Login request for email: {}", req.email());
        var request = new LoginCmd(req.email(), req.password());
        var authResponse = authService.authenticate(request);
        return new LoginResponse(
                authResponse.accessToken(),
                authResponse.expiresAt(),
                authResponse.userId(),
                authResponse.name(),
                authResponse.email(),
                authResponse.role());
    }

    public record LoginRequest(
            @NotEmpty(message = "Email is required")
            @Email(message = "Invalid email address")
            String email,

            @NotEmpty(message = "Password is required")
            String password) {}

    public record LoginResponse(
            String token,
            Instant expiresAt,
            Long userId,
            String name,
            String email,
            String role) {}
}
