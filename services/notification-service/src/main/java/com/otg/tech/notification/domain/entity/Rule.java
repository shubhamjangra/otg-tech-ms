package com.otg.tech.notification.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(name = "rules")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Rule extends AuditAwareBaseEntity {

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "language")
    private String language;

    @Column(name = "trigger_expression")
    private String triggerExpression;

    @ManyToOne
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "template_id", referencedColumnName = "id")
    private Template template;

    @Column(name = "is_retry_enabled")
    @lombok.Builder.Default
    private boolean isRetryEnabled = false;

    @Tolerate
    public Rule() {
        /*
        Making lombok pretend it doesn't exist, i.e., to generate a method which would
        otherwise be skipped due to possible conflicts.
        */
    }

    public boolean execute(NotificationEvent obj) {
        return Objects.nonNull(obj);
        // it has to use the triggerExpression and
        // the object to decide whether this rule is applied or not
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Rule that = (Rule) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
