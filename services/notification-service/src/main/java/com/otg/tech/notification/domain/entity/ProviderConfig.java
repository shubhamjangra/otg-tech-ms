package com.otg.tech.notification.domain.entity;

import com.otg.tech.notification.domain.enums.ConfigDataType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "provider_configs")
@Getter
@Setter
@NoArgsConstructor
public class ProviderConfig extends AuditAwareBaseEntity {

    @Column(name = "key")
    private String key;

    @Column(name = "value")
    private String value;

    @Column(name = "config_data_type")
    @Enumerated(EnumType.STRING)
    private ConfigDataType configDataType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", referencedColumnName = "id")
    private Provider provider;

    public ProviderConfig(String key, String value, ConfigDataType configDataType) {
        this.key = key;
        this.value = value;
        this.configDataType = configDataType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ProviderConfig that = (ProviderConfig) o;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
