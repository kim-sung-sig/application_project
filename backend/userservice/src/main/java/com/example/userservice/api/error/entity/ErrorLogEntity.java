package com.example.userservice.api.error.entity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(name = "error_log")
@Getter @ToString @EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ErrorLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uri;
    private String method;
    private String message;
    private String exceptionName;
    private String stackTrace;

    private String userAgent;
    private String clientIp;
    private LocalDateTime createdAt;

    public static ErrorLogEntity of(Exception e, HttpServletRequest request) {
        return ErrorLogEntity.builder()
                .uri(request.getRequestURI())
                .method(request.getMethod())
                .message(e.getMessage())
                .exceptionName(e.getClass().getSimpleName())
                .stackTrace(getStackTraceAsString(e))
                .userAgent(request.getHeader("User-Agent"))
                .clientIp(request.getRemoteAddr())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private static String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
