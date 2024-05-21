package com.otg.tech.notification.service;

import com.otg.tech.notification.domain.entity.NotificationEvent;
import com.otg.tech.notification.domain.entity.Rule;
import com.otg.tech.notification.repository.RuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.otg.tech.notification.constant.NotificationConstants.LANGUAGE;

@Service
@RequiredArgsConstructor
public class RuleService {

    private final RuleRepository ruleRepository;

    @SuppressWarnings("unused")
    public List<Rule> findMatchingRules(NotificationEvent event) {
        List<Rule> rules = this.ruleRepository.findAllByEventTypeAndLanguage(
                event.getEventType(),
                (String) event.getCustomerData().get(LANGUAGE));
        return rules.stream()
                .filter(r -> r.execute(event)).toList();
    }
}
