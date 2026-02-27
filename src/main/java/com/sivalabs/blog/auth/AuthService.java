package com.sivalabs.blog.auth;

import com.sivalabs.blog.users.UsersAPI;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class AuthService {
    private final AuthenticationManager authManager;
    private final JwtTokenHelper tokenProvider;
    private final UsersAPI usersAPI;

    AuthService(
            AuthenticationManager authManager,
            JwtTokenHelper tokenProvider,
            UsersAPI usersAPI) {
        this.authManager = authManager;
        this.tokenProvider = tokenProvider;
        this.usersAPI = usersAPI;
    }

    public LoginResult authenticate(LoginCmd request) {
        var auth = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        authManager.authenticate(auth);

        var user = usersAPI
                .findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.email()));

        var authToken = tokenProvider.generateToken(user);
        return new LoginResult(
                authToken.token(),
                authToken.expiresAt(),
                user.id(),
                user.name(),
                user.email(),
                user.role().name());
    }
}
