package com.example.demo.util;

import org.springframework.util.ObjectUtils;

public class CommonUtil {

    public static boolean isEmpty(Object obj) {
        return ObjectUtils.isEmpty(obj);
    }

    public static boolean hasEmpty(Object... objs) {
        if (objs == null) return true;

        for (Object obj : objs) {
            if (ObjectUtils.isEmpty(obj))
                return true;
        }

        return false;
    }

}
