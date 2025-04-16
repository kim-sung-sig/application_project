package com.example.userservice.common.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final String CODE = "code";
    public static final String MESSAGE = "message";
    public static final String ERRORS = "errors";
    public static final String RETRY_AFTER = "retryAfterSeconds";

    // 기본 에러 포맷
    private Map<String, Object> createErrorResponse(String message, String code, Map<String, String> errors) {
        Map<String, Object> body = new HashMap<>();
        body.put(CODE, code);
        body.put(MESSAGE, message);

        if (errors != null) {
            body.put(ERRORS, errors);
        }

        return body;
    }

    // 일반적인 예외 처리 (시스템 예외)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        log.error("시스템 오류 발생", e);

        Map<String, Object> body = createErrorResponse(
                "시스템 오류가 발생했습니다.",
                "INTERNAL_SERVER_ERROR",
                null);

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Validation 예외
    @ExceptionHandler(exception = { MethodArgumentNotValidException.class })
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        Map<String, Object> body = createErrorResponse(
                "입력값이 올바르지 않습니다.",
                "VALIDATION_ERROR",
                errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(ValidationException e) {
        Map<String, Object> body = createErrorResponse(
                "입력값이 올바르지 않습니다.",
                "VALIDATION_ERROR",
                e.getErrors());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // EntityNotFoundException 처리
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException e) {
        Map<String, Object> body = createErrorResponse(
                e.getMessage(),
                "ENTITY_NOT_FOUND",
                null);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // 일시적인 예외 처리
    @ExceptionHandler(TemporaryException.class)
    public ResponseEntity<Map<String, Object>> handleTemporaryException(TemporaryException e) {
        Map<String, Object> body = createErrorResponse(
                e.getMessage(),
                "TEMPORARY_ERROR",
                null);

        body.put(RETRY_AFTER, e.getRetryAfterSeconds());
        return new ResponseEntity<>(body, HttpStatus.SERVICE_UNAVAILABLE);
    }

    // BusinessException 예외
    @ExceptionHandler(exception = { BusinessException.class })
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException e) {

        Map<String, Object> body = createErrorResponse(
                e.getMessage(),
                "BUSINESS_ERROR",
                e.getErrors());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

}
