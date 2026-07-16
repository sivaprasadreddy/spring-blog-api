package com.sivalabs.blog.shared.utils;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public final class AuthUtils {
    private AuthUtils() {}

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        var principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            return jwt.getClaim("user_id");
        }
        return null;
    }

    public static Long getCurrentUserIdOrThrow() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("Access denied");
        }
        return currentUserId;
    }
}
