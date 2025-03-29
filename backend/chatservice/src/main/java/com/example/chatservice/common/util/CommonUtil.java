package com.example.chatservice.common.util;

import java.util.Arrays;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class CommonUtil {

    public static boolean isEmpty(Object obj) {
        return ObjectUtils.isEmpty(obj);
    }

    public static boolean isEmpty(String str) {
        return !StringUtils.hasText(str);
    }

    public static boolean hasEmpty(Object... obj) {
        if (obj == null) {
            return true;
        }
        return Arrays.stream(obj).anyMatch(CommonUtil::isEmpty);
    }

    public static boolean hasEmpty(String... str) {
        if (str == null) {
            return true;
        }
        return Arrays.stream(str).anyMatch(CommonUtil::isEmpty);
    }

}
