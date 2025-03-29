package com.example.userservice.test.init;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.userservice.domain.entity.NickNameHistory;
import com.example.userservice.domain.entity.User;
import com.example.userservice.domain.entity.User.UserRole;
import com.example.userservice.domain.entity.User.UserStatus;
import com.example.userservice.domain.repository.history.NickNameHistoryRepository;
import com.example.userservice.domain.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitUserData {

    private final BCryptPasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final NickNameHistoryRepository nickNameHistoryRepository;

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
        if (userRepository.existsByUsername(username)) {
            log.info("User data already exists");
            return;
        }

        NickNameHistory nickNameHistory = new NickNameHistory(username, 1L);
        nickNameHistoryRepository.save(nickNameHistory);

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(UserRole.ADMIN)
                .status(UserStatus.ENABLED)
                .name(username)
                .nickName(username + nickNameHistory.getSeq())
                .email(username + "@example.com")
                .phone("01012345678")
                .build();

        userRepository.save(user);
        log.info("User data initialized");
    }

}
