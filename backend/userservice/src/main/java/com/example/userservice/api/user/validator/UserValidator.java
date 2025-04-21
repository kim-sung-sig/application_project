package com.example.userservice.api.user.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import com.example.userservice.api.user.request.CreateUserCommand;
import com.example.userservice.common.util.CommonUtil;

public class UserValidator {

    public Map<String, String> validate(CreateUserCommand command) {
        Map<String, String> validationErrors = new HashMap<>();

        // username 검사
        this.validUsernameFormat(command.username())
                .ifPresent(msg -> validationErrors.put(CreateUserCommand.FIELD_USERNAME, msg));

        // password 검사
        this.validatePasswordFormat(command.password())
                .ifPresent(msg -> validationErrors.put(CreateUserCommand.FIELD_PASSWORD, msg));
        return validationErrors;
    }

    public Optional<String> validUsernameFormat(String username) {

        // 값 존재 여부
        if (CommonUtil.isEmpty(username)) {
            return Optional.of("아이디를 입력해주세요.");
        }

        // 공백 여부
        if (CommonUtil.hasBlank(username)) {
            return Optional.of("아이디에 공백에 포함될 수 없습니다.");
        }

        // 문자 길이 여부
        if (username.length() < 8 || username.length() > 20) {
            return Optional.of("아이디는 8자 이상 20자 이하만 가능합니다.");
        }

        // 특수 문자 여부
        if (!username.matches("^[a-zA-Z0-9]+$")) {
            return Optional.of("유효한 아이디가 아닙니다. 아이디는 영문 대소문자와 숫자만 사용할 수 있습니다.");
        }

        return Optional.empty();
    }

    public Optional<String> validatePasswordFormat(String password) {
        // 값 존재 여부
        if (CommonUtil.isEmpty(password)) {
            return Optional.of("비밀번호를 입력해주세요.");
        }

        // 공백 여부
        if (CommonUtil.hasBlank(password)) {
            return Optional.of("비밀번호에 공백에 포함될 수 없습니다.");
        }

        // 문자 길이 여부
        if (password.length() < 8 || password.length() > 20) {
            return Optional.of("비밀번호는 8자 이상 20자 이하만 가능합니다.");
        }

        // 특수 문자 여부 => 정규식 8~20자 영문, 숫자, 특수문자 포함, 공백 제외, 대문자
        if (password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,20}$")) {
            return Optional.of("유효한 비밀번호가 아닙니다. 비밀번호는 8자 이상 20자 이하, 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.");
        }

        return Optional.empty();
    }

    public Optional<String> validateNameFormat(String name) {

        // 값 존재 여부
        if (CommonUtil.isEmpty(name)) {
            return Optional.of("이름을 입력해주세요.");
        }

        // 공백 여부
        if (CommonUtil.hasBlank(name)) {
            return Optional.of("이름에 공백에 포함될 수 없습니다.");
        }

        // 문자 길이 여부
        if (name.length() < 2 || name.length() > 10) {
            return Optional.of("이름은 2자 이상 10자 이하만 가능합니다.");
        }

        // 특수 문자 여부
        if (!name.matches("^[a-zA-Z가-힣]{2,10}$")) {
            return Optional.of("유효한 이름이 아닙니다. 이름은 영문 대소문자, 숫자, 한글만 사용할 수 있습니다.");
        }

        return Optional.empty();
    }

    public Optional<String> validateNickNameFormat(String nickName) {

        // 값 존재 여부
        if (CommonUtil.isEmpty(nickName)) {
            return Optional.of("닉네임을 입력해주세요.");
        }

        // 공백 여부
        if (CommonUtil.hasBlank(nickName)) {
            return Optional.of("닉네임에 공백에 포함될 수 없습니다.");
        }

        // 문자 길이 여부
        if (nickName.length() < 2 || nickName.length() > 16) {
            return Optional.of("닉네임은 2자 이상 16자 이하만 가능합니다.");
        }

        // 특수 문자 여부
        if (!nickName.matches("^[a-zA-Z0-9가-힣]+$")) {
            return Optional.of("유효한 닉네임이 아닙니다. 닉네임은 영문 대소문자, 숫자, 한글만 사용할 수 있습니다.");
        }

        return Optional.empty();
    }

    public Optional<String> validateEmailFormat(String email) {
        // 정규식 이메일
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
        if (!pattern.matcher(email).matches()) {
            return Optional.of("이메일 형식이 올바르지 않습니다.");
        }

        return Optional.empty();
    }

}
