package com.example.userservice.common.config.securiry.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.userservice.domain.entity.User;
import com.example.userservice.domain.entity.User.UserRole;
import com.example.userservice.domain.entity.User.UserStatus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SecurityUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private final UUID id;

    private final String username;

    private final String password;

    private final String role;

    private final UserStatus status;

    private final int loginFailCount;

    private final LocalDateTime lastLoginAt;

    private final LocalDateTime createdAt;

    private final boolean isLocked;

    private final boolean isEnabled;

    private final Collection<? extends GrantedAuthority> authorities;


    public SecurityUser(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = user.getRole().name();
        this.status = user.getStatus();
        this.loginFailCount = user.getLoginFailCount();
        this.lastLoginAt = user.getLastLoginAt();
        this.createdAt = user.getCreatedAt();
        this.isLocked = isLocked(user);
        this.isEnabled = isEnabled(user);
        this.authorities = createAuthorities(user.getRole());
    }

    private boolean isLocked(User user) {
        return false;
    }

    private boolean isEnabled(User user) {
        return true;
    }

    private Collection<? extends GrantedAuthority> createAuthorities(UserRole role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

}
