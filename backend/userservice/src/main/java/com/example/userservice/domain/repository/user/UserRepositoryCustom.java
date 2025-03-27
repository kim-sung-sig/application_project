package com.example.userservice.domain.repository.user;

import java.util.Optional;
import java.util.UUID;

import com.example.userservice.domain.model.UserForSecurity;

public interface UserRepositoryCustom {

    Optional<UserForSecurity> findByUsernameForSecurity(String username);

    Optional<UserForSecurity> findByUserIdForSecurity(UUID userId);

}
