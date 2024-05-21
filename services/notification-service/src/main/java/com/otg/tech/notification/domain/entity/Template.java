package com.otg.tech.notification.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(name = "templates")
@Getter
@NoArgsConstructor
public class Template extends AuditAwareBaseEntity {

    @Column(name = "template_code")
    private String templateCode;

    @Column(name = "language")
    private String language;

    @Column(name = "subject")
    private String subject;

    @Column(name = "template", columnDefinition = "BYTEA")
    private byte[] bodyTemplate;

    @Builder
    public Template(String templateCode, String language, String subject, byte[] bodyTemplate) {
        this.templateCode = templateCode;
        this.language = language;
        this.subject = subject;
        this.bodyTemplate = bodyTemplate;
    }

    public void setTemplateDetails(String templateCode, String language, String subject, byte[] bodyTemplate) {
        this.templateCode = templateCode;
        this.language = language;
        this.subject = subject;
        this.bodyTemplate = bodyTemplate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Template that = (Template) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
