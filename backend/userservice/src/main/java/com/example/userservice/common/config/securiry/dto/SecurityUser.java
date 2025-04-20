package com.example.userservice.common.config.securiry.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.userservice.api.user.entity.User;
import com.example.userservice.api.user.entity.User.UserRole;
import com.example.userservice.api.user.entity.User.UserStatus;
import com.example.userservice.common.util.JwtUtil.JwtUserInfo;

/**
 * SecurityUser is a record that represents a user in the security context.
 * It implements Serializable for serialization purposes.
 * It contains fields such as id, username, password, role, status, loginFailCount,
 * lastLoginAt, and createdAt.
 * It also provides methods to create a SecurityUser from a User entity and to check
 * if the user is locked or enabled.
 */
public record SecurityUser(
    UUID id,

    String username,
    UserRole role,

    UserStatus status,
    int loginFailCount,
    LocalDateTime lastLoginAt,
    LocalDateTime createdAt
) implements Serializable {

    private static final long serialVersionUID = 1L;

    public static SecurityUser of(User user) {
        return new SecurityUser(
            user.getId(),
            user.getUsername(),
            user.getRole(),
            user.getStatus(),
            user.getLoginFailCount(),
            user.getLastLoginAt(),
            user.getCreatedAt()
        );
    }

    public static SecurityUser of(JwtUserInfo userInfo) {
        return new SecurityUser(
            userInfo.id(),
            userInfo.username(),
            userInfo.role(),
            null,
            0,
            null,
            null
        );
    }

    public boolean isEnabled() { // 1순위
        boolean unEnabled = Objects.equals(UserStatus.DELETED, status) || Objects.equals(UserStatus.DISABLED, status);
        return !unEnabled;
    }

    public boolean isLocked() { // 2순위
        return Objects.equals(UserStatus.LOCKED, status);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

}
