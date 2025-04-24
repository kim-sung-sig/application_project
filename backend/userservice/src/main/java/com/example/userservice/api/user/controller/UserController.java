package com.example.userservice.api.user.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.userservice.api.user.components.UserResolver;
import com.example.userservice.api.user.entity.User;
import com.example.userservice.api.user.request.CreateUserCommand;
import com.example.userservice.api.user.request.UpdateUserCommand;
import com.example.userservice.api.user.service.command.UserCommandService;
import com.example.userservice.common.config.securiry.model.SecurityUser;
import com.example.userservice.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserResolver userResolver;
    private final UserCommandService userCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse< Void >> createUser(@RequestBody CreateUserCommand command) {
        userCommandService.createUser(command);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #securityUser.id == #targetUserId")
    public ResponseEntity<ApiResponse< Void >> updateUser(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable(name = "id") UUID targetUserId,
            @RequestBody UpdateUserCommand command) {

        User targetUser = userResolver.resolve(targetUserId);
        log.info("사용자 정보 수정 요청 by [{}] targetUserId : {}, command : {}", securityUser.username(), targetUserId, command);

        userCommandService.updateUser(targetUser, command);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #securityUser.id == #targetUserId")
    public ResponseEntity<ApiResponse< Void >> deleteUser(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable(name = "id") UUID targetUserId) {

        User targetUser = userResolver.resolve(targetUserId);
        log.info("사용자 정보 삭제 요청 by [{}] targetUserId : {}", securityUser.username(), targetUserId);

        userCommandService.deleteUser(targetUser);
        return ApiResponse.success();
    }

}
