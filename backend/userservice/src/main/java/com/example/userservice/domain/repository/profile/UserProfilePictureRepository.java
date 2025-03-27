package com.example.userservice.domain.repository.profile;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.userservice.domain.entity.UserProfilePicture;

@Repository
public interface UserProfilePictureRepository extends JpaRepository<UserProfilePicture, UUID>, UserProfilePictureRepositoryCustom {

}
