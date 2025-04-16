package com.example.userservice.api.user.validator;

import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.lang.NonNull;

public class UserValidator {

    public Optional<String> validUsernameFormat(@NonNull String username) {

        // 공백 여부
        if (username.length() != username.replaceAll("\\s+", "").length()) {
            return Optional.of("아이디에 공백에 포함될 수 없습니다.");
        }

        // 문자길이 여부
        if (username.length() < 8 || username.length() > 20) {
            return Optional.of("아이디는 8자 이상 20자 이하만 가능합니다.");
        }

        // 특수문자 여부
        if (!username.matches("^[a-zA-Z0-9]+$")) {
            return Optional.of("아이디는 영문 대소문자와 숫자만 사용할 수 있습니다.");
        }

        return Optional.empty();
    }

    public Optional<String> validateEmailFormat(@NonNull String email) throws Exception {
        // 정규식 이메일
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
        if (!pattern.matcher(email).matches()) {
            return Optional.of("이메일 형식이 올바르지 않습니다.");
        }

        return Optional.empty();
    }

}
