package com.sivalabs.blog.auth;

import com.sivalabs.blog.users.UserDto;
import com.sivalabs.blog.users.UsersAPI;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
class SecurityUserDetailsService implements UserDetailsService {
    private final UsersAPI usersAPI;

    SecurityUserDetailsService(UsersAPI usersAPI) {
        this.usersAPI = usersAPI;
    }

    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String userName) {
        return usersAPI
                .findByEmailWithPassword(userName)
                .map(this::toSecurityUser)
                .orElseThrow(() -> new UsernameNotFoundException("Email " + userName + " not found"));
    }

    private SecurityUser toSecurityUser(UserDto user) {
        return new SecurityUser(user.id(), user.name(), user.email(), user.password(), user.role());
    }
}
