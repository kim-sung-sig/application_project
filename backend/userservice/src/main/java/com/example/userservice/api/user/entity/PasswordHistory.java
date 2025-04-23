package com.example.userservice.api.user.entity;

import java.util.UUID;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.userservice.common.config.jpa.CustomAuditingEntityListener;
import com.example.userservice.common.config.jpa.entity.AuditEntity;
import com.example.userservice.common.config.jpa.entity.AuditableEntity;
import com.example.userservice.common.util.UuidUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@EntityListeners({AuditingEntityListener.class, CustomAuditingEntityListener.class})
@Table(
    name = "dn_password_history",
    indexes = {
        @Index(name = "idx_password_history_user_id", columnList = "user_id"),
        @Index(name = "idx_password_history_created_at", columnList = "created_at"),
    }
)
@Getter @ToString @EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PasswordHistory implements AuditableEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "password")
    private String password;

    @Embedded @Setter
    private AuditEntity audit;

    @PrePersist
    protected void onPrePersist() {
        if (id == null) id = UuidUtil.generate();
        log.debug("User onPrePersist");
    }

    @PostPersist
    protected void onPostPersist() {
    }

    @PreUpdate
    protected void onPreUpdate() {
    }

    @PostUpdate
    protected void onPostUpdate() {
    }

}
