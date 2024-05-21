package com.otg.tech.notification.repository;

import com.otg.tech.notification.domain.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepository extends JpaRepository<Provider, String> {
}
