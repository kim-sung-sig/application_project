package com.example.userservice.api.user.resolver;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.userservice.api.user.request.CreateUserCommand;
import com.example.userservice.api.user.request.UpdateUserCommand;
import com.example.userservice.common.config.securiry.dto.CustomUserDetails;
import com.example.userservice.domain.entity.User;
import com.example.userservice.domain.entity.User.UserRole;
import com.example.userservice.domain.exception.UserNotFoundException;
import com.example.userservice.domain.repository.user.UserRepository;
import com.google.common.base.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCommandResolver {

    private final UserRepository userRepository;

    public CreateUserCommand createUser(CreateUserCommand command) {


        return command;
    }

    public User updateUser(CustomUserDetails userDetails, UUID targetUserId, UpdateUserCommand command) {

        String currentUsername = userDetails.getUsername();

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException());

        if (!Objects.equal(currentUser.getRole(), UserRole.ROLE_ADMIN) && !Objects.equal(currentUser.getId(), targetUserId)) {
            throw new RuntimeException();
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(UserNotFoundException::new);

        // commond에 해당하는 valid 체크

        return targetUser;
    }
}
