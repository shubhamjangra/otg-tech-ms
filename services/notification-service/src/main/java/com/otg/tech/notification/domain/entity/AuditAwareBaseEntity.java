package com.otg.tech.notification.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.Objects;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("unused")
public class AuditAwareBaseEntity extends AbstractBaseEntity {

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    @Getter
    private OffsetDateTime createdAt;

    @Column(name = "last_modified_at")
    @LastModifiedDate
    private OffsetDateTime lastModifiedAt;

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @Column(name = "last_modified_by")
    @LastModifiedBy
    private String lastModifiedBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AuditAwareBaseEntity that = (AuditAwareBaseEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
