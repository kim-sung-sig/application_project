package com.example.userservice.api.user.domain.repository.profile;

import java.util.Optional;
import java.util.UUID;

import com.example.userservice.api.user.domain.model.FilePath;

public interface UserProfilePictureRepositoryCustom {

    Optional<FilePath> findFilePathById(UUID profileId);
}
