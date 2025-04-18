package com.example.userservice.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.userservice.common.entity.BaseEntity;
import com.example.userservice.common.enums.EventType;
import com.example.userservice.common.util.EventPublisher;
import com.example.userservice.common.util.UuidUtil;
import com.example.userservice.domain.event.user.UserEvent;

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
    name = "dn_user",
    indexes = {
        @Index(name = "idx_user_username", columnList = "username", unique = true),
        @Index(name = "idx_user_nickname", columnList = "nick_name", unique = true),
        @Index(name = "idx_user_email", columnList = "email"),
    }
)
@Getter @ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
@EntityListeners(value = {AuditingEntityListener.class})
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class User extends BaseEntity {

    // key
    @Id
    private UUID id;

    // security
    @Column(name = "username", nullable = false, unique = true)
    @Comment("아이디")
    private String username;

    @Column(name = "password")
    @Comment("비밀번호")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Comment("사용자 권한")
    private UserRole role;

    @Column(name = "login_fail_count", columnDefinition = "int default 0")
    @Comment("로그인 실패 횟수")
    private int loginFailCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Comment("사용자 상태")
    private UserStatus status;

    @Column(name = "last_login_at")
    @Comment("마지막 로그인 일시")
    private LocalDateTime lastLoginAt;

    @Column(name = "temp_password")
    @Comment("임시 비밀번호")
    private String tempPassword;

    @Column(name = "temp_password_expired_at")
    @Comment("임시 비밀번호 만료 일시")
    private LocalDateTime tempPasswordExpiredAt;


    // 사용자 정보
    @Column(name = "name", nullable = false)
    @Comment("사용자 이름")
    private String name;

    @Column(name = "nick_name", nullable = false)
    @Comment("사용자 닉네임")
    private String nickName;

    @Column(name = "nick_name_tag", nullable = false, unique = true)
    @Comment("사용자 닉네임 테그")
    private String nickNameTag;

    @Column(name = "email")
    @Comment("사용자 이메일")
    private String email;

    @Column(name = "phone")
    @Comment("사용자 전화번호")
    private String phone;

    // method
    public void changeName(String name) {
        this.name = name;
    }

    public void changeEmail(String email) {
        this.email = email;
    }

    public void changeNickName(String nickName, String nickNameTag) {
        this.nickName = nickName;
        this.nickNameTag = nickNameTag;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void setTempPassword(String tempPassword, int minutes) {
        this.tempPassword = tempPassword;
        this.tempPasswordExpiredAt = LocalDateTime.now().plusMinutes(minutes);
    }

    public void incrementLoginFailureCount() {
        this.loginFailCount++;
    }

    public void changeRole(UserRole role) {
        this.role = role;
    }

    public void userDelete() {
        this.status = UserStatus.DELETED;
    }

    public void userLock() {
        this.status = UserStatus.LOCKED;
    }

    public void loginSuccess() {
        this.lastLoginAt = LocalDateTime.now();
        this.loginFailCount = 0;
    }

    @Override
    @PrePersist
    protected void onPrePersist() {
        super.onPrePersist();
        if (id == null) id = UuidUtil.generate();
        log.debug("User onPrePersist");
    }

    @PostPersist
    protected void onPostPersist() {
        log.debug("User onPostPersist");
        EventPublisher.publish(new UserEvent(EventPublisher.class, this, EventType.CREATED));
        log.debug("User persist EventPublisher.publish");
    }

    @Override
    @PreUpdate
    protected void onPreUpdate() {
        super.onPreUpdate();
    }

    @PostUpdate
    protected void onPostUpdate() {
        EventPublisher.publish(new UserEvent(EventPublisher.class, this, EventType.UPDATED));
    }

    public enum UserRole {
        ROLE_USER("ROLE_USER", "사용자"),
        ROLE_ADMIN("ROLE_ADMIN", "관리자");

        private final String key;
        private final String title;

        private UserRole(String key, String title) {
            this.key = key;
            this.title = title;
        }

        public String getKey() {
            return key;
        }

        public String getTitle() {
            return title;
        }
    }

    public enum UserStatus {
        ENABLED("계정 활성화"),
        LOCKED("계정 잠김"),
        EXPIRED("계정 만료"),
        DISABLED("계정 비활성화"),
        DELETED("계정 삭제");

        private final String title;

        private UserStatus(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

}
