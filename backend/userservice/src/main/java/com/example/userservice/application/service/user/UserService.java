package com.example.userservice.application.service.user;

import java.util.UUID;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.userservice.api.user.request.CreateUserCommand;
import com.example.userservice.api.user.request.UpdateUserCommand;
import com.example.userservice.common.exception.BusinessException;
import com.example.userservice.common.util.CommonUtil;
import com.example.userservice.domain.entity.NickNameHistory;
import com.example.userservice.domain.entity.PasswordHistory;
import com.example.userservice.domain.entity.User;
import com.example.userservice.domain.entity.User.UserRole;
import com.example.userservice.domain.entity.User.UserStatus;
import com.example.userservice.domain.exception.UserNotFoundException;
import com.example.userservice.domain.repository.history.NickNameHistoryRepository;
import com.example.userservice.domain.repository.history.PasswordHistoryRepository;
import com.example.userservice.domain.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final NickNameHistoryRepository nickNameHistoryRepository;
    private final PasswordHistoryRepository passwordHistoryRepository;

    private final PasswordEncoder passwordEncoder;

    // 회원 가입
    @Transactional
    public void createUser(CreateUserCommand command) {
        // 변수 추출
        String username = command.username().trim();
        if (!CommonUtil.isUsernameValid(username))
            throw new BusinessException("Username is not valid");

        String password = command.password().trim();
        if (!CommonUtil.isPasswordValid(password))
            throw new BusinessException("유효한 비밀번호가 아닙니다. 비밀번호는 8자 이상 20자 이하, 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.");

        String name = command.name().trim();
        if (!CommonUtil.isNameValid(name))
            throw new BusinessException("유효한 이름이 아닙니다.");

        String nickName = command.nickName().trim();
        if (!CommonUtil.isNickNameValid(nickName))
            throw new BusinessException("유효한 닉네임이 아닙니다. 닉네임은 2자 이상, 16자 이하로 입력해야 합니다.");

        String email = command.email().trim();
        if (!CommonUtil.isEmailValid(email))
            throw new BusinessException("유효한 이메일이 아닙니다.");

        if (userRepository.existsByUsername(username))
            throw new BusinessException("사용중인 아이디입니다.");

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // nickName 중복 방지를 위한 nickSeq 추출
        long nickSeq = getNickSeq(nickName);

        // 사용자 정보 저장
        User newUser = User.builder()
                .username(username)
                .password(encodedPassword)
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ENABLED)
                .name(name)
                .nickName(nickName + nickSeq)
                .email(email)
                .build();
        userRepository.save(newUser);

        // passwordHistory 저장
        PasswordHistory passwordHistory = PasswordHistory.builder()
                .userId(newUser.getId())
                .password(encodedPassword)
                .build();
        passwordHistoryRepository.save(passwordHistory);
    }

    // 회원 정보 수정
    @Transactional
    public void updateUser(UUID curUserId, UUID targetUserId, UpdateUserCommand command) {
        if (!curUserId.equals(targetUserId)) {
            throw new InsufficientAuthenticationException("권한이 없습니다.");
        }

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException());
        String currentPassword = command.currentPassword();
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException("비밀번호가 일치하지 않습니다.");
        }

        String name = command.name().trim();
        if (name != null && !name.isEmpty()) {
            if (!CommonUtil.isNameValid(name))
                throw new BusinessException("유효한 이름이 아닙니다.");

            user.changeName(name);
        }

        String nickName = command.nickName().trim();
        if (nickName != null && !nickName.isEmpty()) {
            if (!CommonUtil.isNickNameValid(nickName))
                throw new BusinessException("유효한 닉네임이 아닙니다. 닉네임은 2자 이상, 16자 이하로 입력해야 합니다.");

            // nickName 중복 방지를 위한 nickSeq 추출
            long nickSeq = getNickSeq(nickName);

            user.changeNickName(nickName + nickSeq);
        }

        String email = command.email().trim();
        if (email != null && !email.isEmpty()) {
            if (!CommonUtil.isEmailValid(email))
                throw new BusinessException("유효한 이메일이 아닙니다.");

            user.changeEmail(email);
        }

        String newPassword = command.newPassword().trim();
        if (newPassword != null && !newPassword.isEmpty()) {
            if (!CommonUtil.isPasswordValid(newPassword))
                throw new BusinessException("유효한 비밀번호가 아닙니다. 비밀번호는 8자 이상 20자 이하, 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.");

            String encodedPassword = passwordEncoder.encode(newPassword);
            user.changePassword(encodedPassword);

            // passwordHistory 저장
            PasswordHistory passwordHistory = PasswordHistory.builder()
                    .userId(targetUserId)
                    .password(encodedPassword)
                    .build();
            passwordHistoryRepository.save(passwordHistory);
        }

    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            throw new RuntimeException("Authentication not found");
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.userDelete();

        // 세션 무효화
        SecurityContextHolder.clearContext();
    }

    // 회원 정보 조회
    public void getUser() {

    }

    // 회원 정보 조회 with Profile
    public void getUserWithProfile() {

    }

    // 회원 정보 조회 (모든 프로필)
    public void getUserWithProfileAll() {

    }

    // 회원 정보 상세 조회 (개인)
    public void getUserDetail() {

    }

    private Long getNickSeq(String nickName) {
        return nickNameHistoryRepository.findById(nickName)
                .map(exist -> {
                    // 존재하면 +1 한 후 반환
                    long newSeq = exist.incrementSeqAndGet();
                    nickNameHistoryRepository.save(exist); // nickSeq 업데이트
                    return newSeq;
                })
                .orElseGet(() -> {
                    // 존재하지 않으면 새로 저장하고 1을 반환
                    NickNameHistory newNickNameHistory = NickNameHistory.builder()
                            .nickName(nickName)
                            .seq(1L)
                            .build();
                    nickNameHistoryRepository.save(newNickNameHistory); // 새로운 nickNameHistory 저장
                    return 1L;
                });
    }

}
