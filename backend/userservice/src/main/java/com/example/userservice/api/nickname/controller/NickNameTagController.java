package com.example.userservice.api.nickname.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.userservice.api.nickname.service.NickNameTagGenerator;
import com.example.userservice.api.user.components.UserValidator;
import com.example.userservice.common.exception.BusinessException;
import com.example.userservice.common.response.ApiResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/v1/nickname")
@RequiredArgsConstructor
public class NickNameTagController {

    private final NickNameTagGenerator nickNameTagGenerator;
    private final UserValidator userValidator;

    @GetMapping("/preview")
    public ResponseEntity<ApiResponse<String>> previewTag(@Valid @NotBlank @RequestParam String baseNickName) {
        userValidator.validateNickNameFormat(baseNickName)
                .ifPresent(BusinessException::new);

        log.info("닉네임 미리보기 요청: {}", baseNickName);
        String previewTag = nickNameTagGenerator.previewTag(baseNickName);
        log.info("닉네임 미리보기 결과: {}", previewTag);
        return ApiResponse.success(previewTag);
    }

}
