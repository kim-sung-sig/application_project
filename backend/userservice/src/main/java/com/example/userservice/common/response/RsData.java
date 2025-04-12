package com.example.userservice.common.response;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;

public record RsData<T>(
    String code,
    String message,
    T data
) {

    public static <T> ResponseEntity<RsData<T>> success(T data) {
        Locale locale = LocaleContextHolder.getLocale();
        return ResponseEntity.ok(new RsData<>("S-200", "", data));
    }

}
