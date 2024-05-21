package com.otg.tech.notification.repository;

import com.otg.tech.notification.domain.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, String> {

    Optional<Template> findByTemplateCode(String templateCode);
}
