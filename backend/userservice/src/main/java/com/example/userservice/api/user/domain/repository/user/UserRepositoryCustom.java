package com.example.userservice.api.user.domain.repository.user;

import java.util.Optional;
import java.util.UUID;

import com.example.userservice.api.user.domain.model.UserForSecurity;

public interface UserRepositoryCustom {

    Optional<UserForSecurity> findByUsernameForSecurity(String username);

    Optional<UserForSecurity> findByUserIdForSecurity(UUID userId);

}
