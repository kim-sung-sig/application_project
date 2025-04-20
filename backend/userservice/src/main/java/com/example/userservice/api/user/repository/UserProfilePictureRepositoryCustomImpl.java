package com.example.userservice.api.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.userservice.api.user.entity.QUserProfilePicture;
import com.example.userservice.common.enums.IsUsed;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserProfilePictureRepositoryCustomImpl implements UserProfilePictureRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<?> findFilePathById(UUID profileId) {
        QUserProfilePicture profile = QUserProfilePicture.userProfilePicture;

        Object result = queryFactory
            .select(profile.filePath)
            .from(profile)
            .where(
                profile.id.eq(profileId)
                .and(profile.status.ne(IsUsed.DELETED)))
            .fetchOne();

        return Optional.ofNullable(result);
    }

}
