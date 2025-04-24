package com.example.userservice.common.config.securiry.model;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.userservice.api.user.entity.User;
import com.example.userservice.api.user.entity.UserRole;
import com.example.userservice.api.user.entity.UserStatus;
import com.example.userservice.common.util.JwtUtil.JwtUserInfo;
import com.mysema.commons.lang.Assert;

public record SecurityUser(
    UUID id,
    String username,
    String password,
    Collection<? extends GrantedAuthority> authorities,
    UserRole role,
    boolean accountNonExpired,
    boolean credentialsNonExpired,
    boolean accountNonLocked,
    boolean enabled
) implements UserDetails {
    private static final long serialVersionUID = 1L;

    public static SecurityUser of(User user) {
        return new SecurityUser(
            user.getId(),
            user.getUsername(),
            "",
            setAuthorities(user.getRole()),
            user.getRole(),
            true,
            true,
            !isLocked(user.getStatus()),
            isEnabled(user.getStatus())
        );
    }

    public static SecurityUser of(JwtUserInfo userInfo) {
        return new SecurityUser(
            userInfo.id(),
            userInfo.username(),
            "",
            setAuthorities(userInfo.role()),
            userInfo.role(),
            true,
            true,
            true,
            true
        );
    }

    private static Collection<? extends GrantedAuthority> setAuthorities(UserRole role) {
        Assert.notNull(role, "role must not be null");
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    private static boolean isEnabled(UserStatus status) {
        Assert.notNull(status, "status must not be null");
        return !(status == UserStatus.DELETED || status == UserStatus.DISABLED);
    }

    private static boolean isLocked(UserStatus status) {
        Assert.notNull(status, "status must not be null");
        return status == UserStatus.LOCKED;
    }

    // UserDetails 메서드 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
