package com.example.userservice.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
@Table(name = "dn_nick_name_history")
@Getter @ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class NickNameHistory extends BaseEntity {

    @Id
    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "seq")
    private Long seq;

    public long incrementSeqAndGet() {
        this.seq++;
        return this.seq;
    }

    @Override
    @PrePersist
    protected void onPrePersist() {
        super.onPrePersist();
        if (this.seq == null) {
            this.seq = 1L;
        }
    }

}
