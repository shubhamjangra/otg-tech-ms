package com.otg.tech.notification.domain.entity;

import com.otg.tech.notification.domain.enums.ChannelType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "channels")
@Getter
@Setter
@NoArgsConstructor
public class Channel extends AuditAwareBaseEntity {

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Provider> providers = new ArrayList<>();

    @Column(name = "channel_type")
    @Enumerated(EnumType.STRING)
    private ChannelType channelType;

    @Builder
    public Channel(ChannelType channelType) {
        this.channelType = channelType;
    }

    @SuppressWarnings("unused")
    public void addProvider(Provider provider) {
        this.providers.add(provider);
        provider.setChannel(this);
    }

    @SuppressWarnings("unused")
    public void removeProvider(Provider provider) {
        this.providers.remove(provider);
        provider.setChannel(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Channel that = (Channel) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
