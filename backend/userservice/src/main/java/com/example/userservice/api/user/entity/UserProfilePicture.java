package com.example.userservice.api.user.entity;

import java.util.UUID;

import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.userservice.common.config.jpa.CustomAuditingEntityListener;
import com.example.userservice.common.config.jpa.entity.AuditEntity;
import com.example.userservice.common.config.jpa.entity.AuditableEntity;
import com.example.userservice.common.enums.IsUsed;
import com.example.userservice.common.util.UuidUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
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
    name = "dn_user_profile_picture",
    indexes = {
        @Index(name = "idx_user_profile_picture_user_id", columnList = "user_id"),
        @Index(name = "idx_user_profile_picture_status", columnList = "status"),})
@Getter @ToString @EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Builder
public class UserProfilePicture implements AuditableEntity {

    // key
    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private IsUsed status;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_path")
    @Comment("파일 저장 경로")
    private String filePath;

    @Column(name = "file_extension")
    private String fileExtension;

    @Column(name = "file_url")
    private String fileUrl;

    @Embedded @Setter
    private AuditEntity audit;

    @PrePersist
    protected void onPrePersist() {
        if (id == null) id = UuidUtil.generate();
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
