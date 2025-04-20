package com.example.userservice.api.user.entity;

import java.util.UUID;

import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.userservice.common.entity.BaseEntity;
import com.example.userservice.common.enums.IsUsed;
import com.example.userservice.common.util.UuidUtil;

import jakarta.persistence.Column;
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
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(
    name = "dn_user_profile_picture",
    indexes = {
        @Index(name = "idx_user_profile_picture_user_id", columnList = "user_id"),
        @Index(name = "idx_user_profile_picture_status", columnList = "status"),})
@EntityListeners(AuditingEntityListener.class)
@Getter @ToString(callSuper = true) @EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Builder
public class UserProfilePicture extends BaseEntity {

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

    @Override
    @PrePersist
    protected void onPrePersist() {
        super.onPrePersist();
        if (id == null) id = UuidUtil.generate();
    }

    @PostPersist
    protected void onPostPersist() {
    }

    @Override
    @PreUpdate
    protected void onPreUpdate() {
        super.onPreUpdate();
    }

    @PostUpdate
    protected void onPostUpdate() {

    }

}
