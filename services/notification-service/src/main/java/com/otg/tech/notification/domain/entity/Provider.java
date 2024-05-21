package com.otg.tech.notification.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "providers")
@Getter
@Setter
@NoArgsConstructor
public class Provider extends AuditAwareBaseEntity {

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private final List<ProviderConfig> providerConfigs = new ArrayList<>();

    @Column(name = "name")
    private String name;

    @Column(name = "is_active")
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    private Channel channel;

    @Builder
    public Provider(String name, Channel channel) {
        this.name = name;
        this.channel = channel;
    }

    public void addConfig(ProviderConfig providerConfig) {
        this.providerConfigs.add(providerConfig);
        providerConfig.setProvider(this);
    }

    @SuppressWarnings("unused")
    public Provider removeConfig(ProviderConfig providerConfig) {
        this.providerConfigs.remove(providerConfig);
        providerConfig.setProvider(null);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Provider that = (Provider) o;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
