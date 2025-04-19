package com.example.userservice.common.response;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    String code,
    String message,
    T data
) {

    public static <T> ResponseEntity<ApiResponse<T>> success() {
        Locale locale = LocaleContextHolder.getLocale();
        return ResponseEntity.ok(new ApiResponse<>("S-200", "", null));
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        Locale locale = LocaleContextHolder.getLocale();
        return ResponseEntity.ok(new ApiResponse<>("S-200", "", data));
    }
}
