package com.example.carsharingonline.security;

import com.example.carsharingonline.model.User;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    private static final Authentication authentication = SecurityContextHolder
            .getContext().getAuthentication();

    public static boolean hasRole(String role) {
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));
        }
        return false;
    }

    public static Optional<Long> getCurrentUserId() {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            return Optional.ofNullable(user.getId());
        }
        return null;
    }
}
