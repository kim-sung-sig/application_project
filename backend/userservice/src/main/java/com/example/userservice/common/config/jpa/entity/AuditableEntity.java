package com.example.userservice.common.config.jpa.entity;

/*
 * AuditableEntity.java
 * spring audiatable 을 대체하기 위한 인터페이스
 */
public interface AuditableEntity {
    AuditEntity getAudit();
    void setAudit(AuditEntity audit);
}
