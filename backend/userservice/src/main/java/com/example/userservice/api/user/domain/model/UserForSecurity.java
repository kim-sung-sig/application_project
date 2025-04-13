package com.example.userservice.api.user.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.userservice.api.user.domain.entity.User;
import com.example.userservice.api.user.domain.entity.User.UserRole;
import com.example.userservice.api.user.domain.entity.User.UserStatus;

public record UserForSecurity(
    UUID id,

    String username,
    String password,
    UserRole role,

    UserStatus status,
    int loginFailCount,

    LocalDateTime lastLoginAt,
    LocalDateTime createdAt
) implements Serializable {

    public static UserForSecurity of(User user) {
        return new UserForSecurity(
            user.getId(),
            user.getUsername(),
            user.getPassword(),
            user.getRole(),
            user.getStatus(),
            user.getLoginFailCount(),
            user.getLastLoginAt(),
            user.getCreatedAt()
        );
    }

}
