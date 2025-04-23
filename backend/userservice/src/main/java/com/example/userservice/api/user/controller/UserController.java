package com.example.userservice.api.user.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.userservice.api.user.components.UserResolver;
import com.example.userservice.api.user.entity.User;
import com.example.userservice.api.user.entity.UserRole;
import com.example.userservice.api.user.request.CreateUserCommand;
import com.example.userservice.api.user.request.UpdateUserCommand;
import com.example.userservice.api.user.service.command.UserCommandService;
import com.example.userservice.api.user.service.query.UserQueryService;
import com.example.userservice.common.config.securiry.dto.SecurityUser;
import com.google.common.base.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserResolver userResolver;
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @GetMapping
    public ResponseEntity<Void> getUserList(@AuthenticationPrincipal SecurityUser securityUser) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Void> getUser(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable(name = "id") Long targetUserId) {
        log.info("사용자 정보 조회 요청");
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody CreateUserCommand command) {
        userCommandService.createUser(command);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable(name = "id") UUID targetUserId,
            @RequestBody UpdateUserCommand command) {

        log.info("사용자 정보 수정 요청");
        log.info("targetUserId : {}", targetUserId);
        log.info("command : {}", command);

        if (Objects.equal(securityUser.status(), UserRole.ROLE_ADMIN)) log.info("관리자 권한으로 사용자 정보 수정");
        else if (Objects.equal(securityUser.id(), targetUserId)) log.info("일반 사용자 권한으로 사용자 정보 수정");
        else {
            log.info("권한 없음");
            return ResponseEntity.status(403).build();
        }

        User targetUser = userResolver.resolve(targetUserId);
        userCommandService.updateUser(targetUser, command);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable(name = "id") UUID targetUserId) {

        log.info("사용자 정보 삭제 요청");
        log.info("targetUserId : {}", targetUserId);

        if (Objects.equal(securityUser.status(), UserRole.ROLE_ADMIN)) log.info("관리자 권한으로 사용자 정보 삭제");
        else if (Objects.equal(securityUser.id(), targetUserId)) log.info("일반 사용자 권한으로 사용자 정보 삭제");
        else {
            log.info("권한 없음");
            return ResponseEntity.status(403).build();
        }

        User targetUser = userResolver.resolve(targetUserId);
        userCommandService.deleteUser(targetUser);
        return ResponseEntity.ok().build();
    }

}
