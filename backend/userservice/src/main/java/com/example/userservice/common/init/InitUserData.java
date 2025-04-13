package com.example.userservice.common.init;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.userservice.api.user.domain.entity.NickNameHistory;
import com.example.userservice.api.user.domain.entity.User;
import com.example.userservice.api.user.domain.entity.User.UserRole;
import com.example.userservice.api.user.domain.entity.User.UserStatus;
import com.example.userservice.api.user.domain.repository.history.NickNameHistoryRepository;
import com.example.userservice.api.user.domain.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitUserData {

    private final UserRepository userRepository;
    private final NickNameHistoryRepository nickNameHistoryRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner init() {
        return (args) -> {
            initUser("admin", "123456");
            initUser("master", "123456");

            List<User> users = userRepository.findAll(Sort.by(Sort.Order.asc("id")));
            for (User user : users) {
                log.info("User: {}", user);
            }

        };
    }

    private void initUser(String username, String password) {
        userRepository.findByUsername(username)
                .ifPresentOrElse(user -> {
                    log.info("User data already exists");
                    user.changeRole(UserRole.ROLE_USER);
                    userRepository.save(user);
                }, () -> {
                    NickNameHistory nickNameHistory = new NickNameHistory(username, 1L);
                    nickNameHistoryRepository.save(nickNameHistory);

                    User user = User.builder()
                            .username(username)
                            .password(passwordEncoder.encode(password))
                            .role(UserRole.ROLE_USER)
                            .status(UserStatus.ENABLED)
                            .name(username)
                            .nickName(username + nickNameHistory.getSeq())
                            .email(username + "@example.com")
                            .phone("01012345678")
                            .build();

                    userRepository.save(user);
                    log.info("User data initialized");
                });

    }

}
