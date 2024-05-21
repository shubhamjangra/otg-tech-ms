package com.otg.tech.notification.domain.entity;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import org.hibernate.Hibernate;

import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
public class AbstractBaseEntity {

    @Getter
    @Id
    private final String id;
    @Version
    @SuppressWarnings("unused")
    private int version;

    public AbstractBaseEntity() {
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AbstractBaseEntity that = (AbstractBaseEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }
}
