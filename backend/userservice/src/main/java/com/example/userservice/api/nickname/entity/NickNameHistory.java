package com.example.userservice.api.nickname.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.userservice.common.config.jpa.CustomAuditingEntityListener;
import com.example.userservice.common.config.jpa.entity.AuditEntity;
import com.example.userservice.common.config.jpa.entity.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
@Table(name = "dn_nick_name_history")
@Getter @ToString @EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
/**
 * NickNameHistory
 * <p>
 * This entity represents the history of nicknames for a user.
 * It includes the nickname and a sequence number to track changes.
 * </p>
 */
public class NickNameHistory implements AuditableEntity {

    @Id
    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "seq")
    private Long seq;

    @Embedded @Setter
    private AuditEntity audit;

    public long incrementSeqAndGet() {
        this.seq++;
        return this.seq;
    }

    @PrePersist
    protected void onPrePersist() {
        if (this.seq == null) {
            this.seq = 1L;
        }
    }

}
