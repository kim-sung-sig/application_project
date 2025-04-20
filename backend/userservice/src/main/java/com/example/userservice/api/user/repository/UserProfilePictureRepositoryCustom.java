package com.example.userservice.api.user.repository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfilePictureRepositoryCustom {

    Optional<?> findFilePathById(UUID profileId);
}
