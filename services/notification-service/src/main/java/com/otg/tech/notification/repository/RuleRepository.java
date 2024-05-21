package com.otg.tech.notification.repository;

import com.otg.tech.notification.domain.entity.Rule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RuleRepository extends JpaRepository<Rule, String> {

    List<Rule> findAllByEventTypeAndLanguage(String eventType, String language);
}
