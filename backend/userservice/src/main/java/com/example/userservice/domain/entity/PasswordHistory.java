package com.example.userservice.domain.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
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
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(
    name = "dn_password_history",
    indexes = {
        @Index(name = "idx_password_history_user_id", columnList = "user_id"),
        @Index(name = "idx_password_history_created_at", columnList = "created_at"),
    }
)
@Getter @ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PasswordHistory extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "password")
    private String password;

    @PrePersist
    protected void onPrePersist() {
        super.onPrePersist();
        log.debug("User onPrePersist");
    }

    @PostPersist
    protected void onPostPersist() {
    }

    @PreUpdate
    protected void onPreUpdate() {
        super.onPreUpdate();
    }

    @PostUpdate
    protected void onPostUpdate() {
    }

}
