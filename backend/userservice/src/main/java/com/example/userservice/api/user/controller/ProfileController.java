package com.example.userservice.api.user.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.userservice.api.user.service.UserProfilePictureService;
import com.example.userservice.common.config.securiry.dto.SecurityUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfilePictureService userProfilePictureService;

    /**
     * 화면 프로필 이미지 전송용
     * 
     * @param userDetails
     * @return
     */
    @GetMapping("/profile/{profileId}")
    public ResponseEntity<Resource> getProfileResource(
            @PathVariable Long profileId,
            @AuthenticationPrincipal SecurityUser userDetails) {

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{profileId}/download")
    public ResponseEntity<Void> getUserProfileDetails(@AuthenticationPrincipal SecurityUser userDetails, @PathVariable Long userId) {

        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile/{profileId}/details")
    public ResponseEntity<Void> getUserProfilesDetail(@AuthenticationPrincipal SecurityUser userDetails, @PathVariable Long userId) {

        return ResponseEntity.ok().build();
    }

    

    @PostMapping("/profile")
    public ResponseEntity<Void> createProfile(@AuthenticationPrincipal SecurityUser userDetails) {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/profile/{profileId}")
    public ResponseEntity<Void> updateProfile(@AuthenticationPrincipal SecurityUser userDetails) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/profile/{profileId}")
    public ResponseEntity<Void> deleteProfile(@AuthenticationPrincipal SecurityUser userDetails) {
        return ResponseEntity.ok().build();
    }

    /*
     * 유저 mapping 용
     */
    @GetMapping("/user/{userId}/profile")
    public ResponseEntity<Void> getUserProfiles(@AuthenticationPrincipal SecurityUser userDetails) {
        return ResponseEntity.ok().build();
    }

}
