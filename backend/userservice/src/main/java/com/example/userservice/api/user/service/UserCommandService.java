package com.example.userservice.api.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.userservice.api.user.components.NickNameTagGenerator;
import com.example.userservice.api.user.request.CreateUserCommand;
import com.example.userservice.api.user.request.UpdateUserCommand;
import com.example.userservice.common.exception.BusinessException;
import com.example.userservice.common.util.CommonUtil;
import com.example.userservice.common.util.PasswordUtil;
import com.example.userservice.domain.entity.User;
import com.example.userservice.domain.entity.User.UserRole;
import com.example.userservice.domain.entity.User.UserStatus;
import com.example.userservice.domain.repository.user.UserRepository;
import com.google.common.base.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository userRepository;
    private final NickNameTagGenerator nickNameTagGenerator;

    // 회원 가입
    @Transactional
    public void createUser(CreateUserCommand command) {

        // 변수 추출
        String username = command.username().trim();
        if (!CommonUtil.isUsernameValid(username)) {
            throw new BusinessException("Username is not valid");
        }

        String password = command.password().trim();
        if (!CommonUtil.isPasswordValid(password)) {
            throw new BusinessException("유효한 비밀번호가 아닙니다. 비밀번호는 8자 이상 20자 이하, 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.");
        }

        String name = command.name().trim();
        if (!CommonUtil.isNameValid(name)) {
            throw new BusinessException("유효한 이름이 아닙니다.");
        }

        String nickName = command.nickName().trim();
        if (!CommonUtil.isNickNameValid(nickName)) {
            throw new BusinessException("유효한 닉네임이 아닙니다. 닉네임은 2자 이상, 16자 이하로 입력해야 합니다.");
        }

        String email = command.email().trim();
        if (!CommonUtil.isEmailValid(email)) {
            throw new BusinessException("유효한 이메일이 아닙니다.");
        }

        if (userRepository.existsByUsername(username)) {
            throw new BusinessException("사용중인 아이디입니다.");
        }

        String nickNameTag = nickNameTagGenerator.generateTag(nickName);

        // create user entity
        User newUser = User.builder()
                .username(username)
                .password(PasswordUtil.encode(password)) // 비밀번호 암호화
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ENABLED)
                .name(name)
                .nickName(nickName)
                .nickNameTag(nickNameTag)
                .email(email)
                .build();
        
        // 사용자 정보 저장
        userRepository.save(newUser);

        // TODO 도메인 이벤트에서 처리
        // passwordHistory 저장
        // PasswordHistory passwordHistory = PasswordHistory.builder()
        //         .userId(newUser.getId())
        //         .password(encodedPassword)
        //         .build();
        // passwordHistoryRepository.save(passwordHistory);
    }

    // 회원 정보 수정
    @Transactional
    public void updateUser(User targetUser, UpdateUserCommand command) {

        if (!Objects.equal(targetUser.getName(), command.name())) {
            targetUser.changeName(command.name());
        }

        if (!Objects.equal(targetUser.getName(), command.nickName())) {
            String newNickName = command.nickName();
            String newNickNameTag = nickNameTagGenerator.generateTag(newNickName);
            targetUser.changeNickName(newNickName, newNickNameTag);
        }

        if (!Objects.equal(targetUser.getEmail(), command.email())) {
            targetUser.changeEmail(command.email());
        }

        String newPassword = command.newPassword().trim();
        if (newPassword != null && !newPassword.isEmpty()) {
            targetUser.changePassword(PasswordUtil.encode(newPassword));
        }

        userRepository.save(targetUser);
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(User targetUser) {
        targetUser.userDelete();
        userRepository.save(targetUser);
    }

}
