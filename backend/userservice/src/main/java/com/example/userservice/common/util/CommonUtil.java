package com.example.userservice.common.util;

import java.util.regex.Pattern;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class CommonUtil {

    public static boolean isEmpty(Object obj) {
        if (obj == null)
            return true;

        if (obj instanceof String str)
            return !StringUtils.hasText(str);

        return ObjectUtils.isEmpty(obj);
    }

    public static boolean isPasswordValid(String password) {
        // 정규식 8~20자 영문, 숫자, 특수문자 포함, 공백 제외, 대문자
        if (password == null)
            return false;
        Pattern pattern = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,20}$");
        return pattern.matcher(password).matches();
    }

    public static boolean isEmailValid(String email) {
        // 정규식 이메일
        if (email == null)
            return false;
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
        return pattern.matcher(email).matches();
    }

    public static boolean isPhoneValid(String phone) {
        // 정규식 전화번호
        if (phone == null)
            return false;
        Pattern pattern = Pattern.compile("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$");
        return pattern.matcher(phone).matches();
    }

    public static boolean isNickNameValid(String nickName) {
        // 정규식 닉네임
        if (nickName == null)
            return false;
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9가-힣]{2,16}$");
        return pattern.matcher(nickName).matches();
    }

    public static boolean isNameValid(String name) {
        // 정규식 이름
        if (name == null)
            return false;
        Pattern pattern = Pattern.compile("^[a-zA-Z가-힣]{2,10}$");
        return pattern.matcher(name).matches();
    }

    public static boolean isUsernameValid(String username) {
        // 정규식 아이디
        if (username == null)
            return false;
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{8,20}$");
        return pattern.matcher(username).matches();
    }

}
