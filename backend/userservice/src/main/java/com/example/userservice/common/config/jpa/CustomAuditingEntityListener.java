package com.example.userservice.common.config.jpa;

import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.userservice.common.config.jpa.entity.AuditEntity;
import com.example.userservice.common.config.jpa.entity.AuditableEntity;
import com.example.userservice.common.util.CommonUtil;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Component
public class CustomAuditingEntityListener {

    @PrePersist
    public void setCreatedInfo(Object target) {
        if (target instanceof AuditableEntity auditable) {
            AuditEntity audit = AuditEntity.builder()
                    .createdAt(LocalDateTime.now())
                    .createdBy(this.getCurrentAuditor())
                    .build();

            auditable.setAudit(audit);
        }
    }

    @PreUpdate
    public void setUpdatedInfo(Object target) {
        if (target instanceof AuditableEntity auditable) {
            AuditEntity audit = auditable.getAudit();

            if (CommonUtil.isEmpty(audit)) {
                audit = new AuditEntity();
                audit.setCreatedBy(this.getCurrentAuditor());
                audit.setCreatedAt(LocalDateTime.now());
            }

            audit.setUpdatedAt(LocalDateTime.now());
            audit.setUpdatedBy(getCurrentAuditor());

            auditable.setAudit(audit);
        }
    }

    private String getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (CommonUtil.isEmpty(auth) || !auth.isAuthenticated()) return "SYSTEM";
        return auth.getName();
    }
}
