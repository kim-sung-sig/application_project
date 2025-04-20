package com.example.userservice.api.user.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.userservice.api.user.entity.UserProfilePicture;

@Repository
public interface UserProfilePictureRepository extends JpaRepository<UserProfilePicture, UUID>, UserProfilePictureRepositoryCustom {

}
