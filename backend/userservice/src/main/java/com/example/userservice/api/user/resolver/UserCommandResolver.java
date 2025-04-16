package com.example.userservice.api.user.resolver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.userservice.api.user.request.CreateUserCommand;
import com.example.userservice.api.user.request.UpdateUserCommand;
import com.example.userservice.api.user.validator.UserValidator;
import com.example.userservice.common.config.securiry.dto.CustomUserDetails;
import com.example.userservice.common.exception.BusinessException;
import com.example.userservice.common.exception.ValidationException;
import com.example.userservice.common.util.CommonUtil;
import com.example.userservice.domain.entity.User;
import com.example.userservice.domain.entity.User.UserRole;
import com.example.userservice.domain.exception.UserNotFoundException;
import com.example.userservice.domain.repository.user.UserRepository;
import com.google.common.base.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCommandResolver {

    private final UserValidator userValidator = new UserValidator();
    private final UserRepository userRepository;

    public CreateUserCommand createUser(CreateUserCommand command) {
        Map<String, String> validationErrors = new HashMap<>();
        // username 검사
        userValidator.validUsernameFormat(command.username())
                .ifPresent(msg -> validationErrors.put(CreateUserCommand.FIELD_USERNAME, msg));

        // ...

        if (!CommonUtil.isEmpty(validationErrors)) {
            throw new ValidationException(validationErrors);
        }

        // String password = command.password().trim();
        // if (!CommonUtil.isPasswordValid(password)) {
        //     throw new BusinessException("유효한 비밀번호가 아닙니다. 비밀번호는 8자 이상 20자 이하, 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.");
        // }

        // String name = command.name().trim();
        // if (!CommonUtil.isNameValid(name)) {
        //     throw new BusinessException("유효한 이름이 아닙니다.");
        // }

        // String nickName = command.nickName().trim();
        // if (!CommonUtil.isNickNameValid(nickName)) {
        //     throw new BusinessException("유효한 닉네임이 아닙니다. 닉네임은 2자 이상, 16자 이하로 입력해야 합니다.");
        // }

        // String email = command.email().trim();
        // if (!CommonUtil.isEmailValid(email)) {
        //     throw new BusinessException("유효한 이메일이 아닙니다.");
        // }

        // 중복 아이디 검사
        if (userRepository.existsByUsername(command.username())) {
            throw new BusinessException("사용중인 아이디입니다.");
        }

        // 검증된 이메일인지 검사

        // ...

        return command;
    }

    public User updateUser(CustomUserDetails userDetails, UUID targetUserId, UpdateUserCommand command) {

        String currentUsername = userDetails.getUsername();

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException());

        if (!Objects.equal(currentUser.getRole(), UserRole.ROLE_ADMIN) && !Objects.equal(currentUser.getId(), targetUserId)) {
            throw new RuntimeException();
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(UserNotFoundException::new);

        // commond에 해당하는 valid 체크

        return targetUser;
    }

}
