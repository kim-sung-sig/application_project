package com.example.userservice.domain.repository.profile;

import java.util.Optional;
import java.util.UUID;

import com.example.userservice.domain.model.FilePath;

public interface UserProfilePictureRepositoryCustom {

    Optional<FilePath> findFilePathById(UUID profileId);
}
