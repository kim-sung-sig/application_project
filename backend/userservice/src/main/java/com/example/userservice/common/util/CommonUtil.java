package com.example.userservice.common.util;

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

    public static boolean hasBlank(String str) {
        return StringUtils.containsWhitespace(str);
    }

}
