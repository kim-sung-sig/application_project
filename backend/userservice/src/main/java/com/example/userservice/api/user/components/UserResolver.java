package com.example.userservice.api.user.components;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.userservice.api.user.entity.User;
import com.example.userservice.api.user.entity.exception.UserNotFoundException;
import com.example.userservice.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserResolver {

    private final UserRepository userRepository;

    /**
     * 사용자 ID로 사용자 조회
     *
     * @param targetUserId 조회할 사용자 ID
     * @return 조회된 사용자
     */
    public User resolve(UUID targetUserId) {
        return userRepository.findById(targetUserId)
                .filter(User::isActive)
                .orElseThrow(UserNotFoundException::new);

    }

}
