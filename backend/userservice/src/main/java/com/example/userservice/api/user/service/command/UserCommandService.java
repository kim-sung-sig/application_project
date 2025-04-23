package com.example.userservice.api.user.service.command;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.userservice.api.nickname.service.NickNameTagGenerator;
import com.example.userservice.api.user.components.UserValidator;
import com.example.userservice.api.user.entity.User;
import com.example.userservice.api.user.entity.UserRole;
import com.example.userservice.api.user.entity.UserStatus;
import com.example.userservice.api.user.repository.UserRepository;
import com.example.userservice.api.user.request.CreateUserCommand;
import com.example.userservice.api.user.request.UpdateUserCommand;
import com.example.userservice.common.exception.BusinessException;
import com.example.userservice.common.exception.ValidationException;
import com.example.userservice.common.util.CommonUtil;
import com.example.userservice.common.util.PasswordUtil;
import com.google.common.base.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository userRepository;

    private final UserValidator userValidator;
    private final NickNameTagGenerator nickNameTagGenerator;

    // 회원 가입
    @Transactional
    public void createUser(CreateUserCommand command) {

        // 값 유효성 검사
        Map<String, String> validationErrors = userValidator.validate(command);
        if (!CommonUtil.isEmpty(validationErrors)) {
            throw new ValidationException(validationErrors);
        }

        // 변수 추출
        String username = command.username();
        String password = command.password();
        String name = command.name();
        String nickName = command.nickName();
        String email = command.email();

        // DB 및 이메일 검증 검사
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

        userRepository.save(targetUser); // 명시적으로 저장 (필요하지 않을 수도 있음)
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(User targetUser) {
        targetUser.userDelete();
        userRepository.save(targetUser);
    }

}
