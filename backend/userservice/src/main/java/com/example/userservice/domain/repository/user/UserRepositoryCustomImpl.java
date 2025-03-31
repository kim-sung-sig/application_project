package com.example.userservice.domain.repository.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.userservice.domain.entity.QUser;
import com.example.userservice.domain.entity.User.UserStatus;
import com.example.userservice.domain.model.UserForSecurity;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Transactional(readOnly = true)
    @Override
    public Optional<UserForSecurity> findByUsernameForSecurity(String username) {
        QUser user = QUser.user;

        UserForSecurity result = queryFactory
            .select(Projections.constructor(UserForSecurity.class,
                user.id
                , user.username
                , user.password
                , user.role
                , user.status
                , user.loginFailCount
                , user.lastLoginAt
                , user.createdAt))
            .from(user)
            .where(
                user.username.eq(username)
                .and(user.status.ne(UserStatus.DELETED)))
            .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<UserForSecurity> findByUserIdForSecurity(UUID userId) {
        QUser user = QUser.user;

        UserForSecurity result = queryFactory
            .select(Projections.constructor(UserForSecurity.class,
                user.id
                , user.username
                , user.password
                , user.role
                , user.status
                , user.loginFailCount
                , user.lastLoginAt
                , user.createdAt))
            .from(user)
            .where(
                user.id.eq(userId)
                .and(user.status.ne(UserStatus.DELETED)))
            .fetchOne();

        return Optional.ofNullable(result);
    }

}
