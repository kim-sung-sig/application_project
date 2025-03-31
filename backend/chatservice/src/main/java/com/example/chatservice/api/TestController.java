package com.example.chatservice.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chatservice.common.config.security.CustomUserDetails;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
public class TestController {

    @GetMapping("/test")
    public Mono<String> getMethodName(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("test, principal: {}", userDetails);
        return Mono.just("hello");
    }

}
