package com.otg.tech.notification.scheduler;

import com.otg.tech.notification.domain.entity.Notification;
import com.otg.tech.notification.domain.entity.NotificationEvent;
import com.otg.tech.notification.domain.enums.NotificationEventStatus;
import com.otg.tech.notification.domain.response.cache.RuleCacheResponse;
import com.otg.tech.notification.repository.NotificationEventRepository;
import com.otg.tech.notification.repository.NotificationRepository;
import com.otg.tech.notification.repository.RuleRepository;
import com.otg.tech.notification.service.ProviderService;
import com.otg.tech.notification.service.http.Message;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.otg.tech.notification.constant.CacheKeys.FIND_RULE_CACHE;
import static com.otg.tech.notification.constant.CacheKeys.FIND_RULE_KEY;
import static com.otg.tech.notification.domain.response.cache.RuleCacheResponse.getRuleCacheResponse;
import static com.otg.tech.notification.template.TemplateEngine.mustacheEngine;

@Service
@Slf4j
@RequiredArgsConstructor
@Observed
public class NotificationPoller {

    private final NotificationEventRepository notificationEventRepository;
    private final ProviderService providerService;
    private final NotificationRepository notificationRepository;
    private final CacheManager cacheManager;
    private final RuleRepository ruleRepository;
    @Value("${notification.service.poll.batch.size:5}")
    protected int batchSize;
    @Value("${notification.service.retry.attempts.count:3}")
    protected int retryAttemptsCount;
    @Value("${notification.service.retry.after.minutes:5}")
    protected int retryAfterMinutes;

    @SuppressWarnings("unchecked")
    private static <T> Class<T> generifyClass() {
        return (Class<T>) List.class;
    }

    @Transactional
    @Async
    @Observed
    public void sendNotifications() {
        log.info("Notification poller execution started. Batch size {}", batchSize);
        final var eventsToProcess = this.notificationEventRepository
                .fetchAndUpdateEventsStateToProcessing(batchSize);
        if (eventsToProcess.isEmpty()) {
            log.info("No notification events found to process so returning");
            return;
        }
        log.info("Updated state of {} events to processing. Events updated ids are {}",
                eventsToProcess.size(), eventsToProcess.stream()
                        .map(NotificationEvent::getId)
                        .toList());

        for (NotificationEvent event : eventsToProcess) {
            final var eventId = event.getId();
            try {
                log.info("Finding matching rules for event {}", eventId);
                final var matchingRules = getRules(event);
                if (matchingRules.isEmpty()) {
                    event.markProcessed();
                    log.info("Found 0 rules that match event {}", eventId);
                    continue;
                }
                log.info("Found {} rules that match event {} with ruleId {}",
                        matchingRules.size(), eventId, matchingRules.get(0).id());

                for (RuleCacheResponse matchingRule : matchingRules) {
                    // Gathering rule details
                    final var template = matchingRule.template();
                    final var optionalProvider = matchingRule.getActiveProvider();
                    if (optionalProvider.isEmpty()) {
                        log.warn("No provider exists for notification_event {} and rule {}", eventId,
                                matchingRule.id());
                        continue;
                    }
                    final var provider = optionalProvider.get();

                    // Hydrating template and subject
                    log.info("Hydrating template {} for notification event {}", template.id(), eventId);
                    String hydratedTemplate = template.hydrateTemplate(event, mustacheEngine());
                    String hydratedSubject = template.hydrateSubject(event, mustacheEngine());
                    log.info("Hydrated template {} for notification event {}", template.id(), eventId);

                    // Preserving notification
                    final var notification = new Notification(event, template, provider, hydratedTemplate,
                            null, matchingRule);
                    notificationRepository.save(notification);

                    // Sending notification
                    log.info("Sending notification {} using {} provider with id {}",
                            eventId, provider.name(), provider.id());
                    providerService.sendNotification(provider, event, new Message(template.templateCode(),
                            hydratedSubject, hydratedTemplate));

                    // Marking event as SUCCESSFULLY_PROCESSED and UNREAD
                    log.info("Sent notification {} using {} provider with id {}",
                            eventId, provider.name(), provider.id());
                    event.markProcessed();
                    notification.markUnRead();
                }
                if (event.isProcessing()) {
                    event.markProcessed();
                }
            } catch (Exception e) {
                log.warn("Failed to process notification event {}. Exception is", eventId, e);
                event.markFailed();
                retryForFailedEvents(event);
            }
        }
        log.info("Notification poller execution finished");
    }

    private void retryForFailedEvents(NotificationEvent event) {
        if (event.getRetryAttempts() < retryAttemptsCount) {
            getRules(event).stream().filter(RuleCacheResponse::isRetryEnabled).forEach(pre -> {
                log.info("Retrying notification event {}", event.getId());

                event.setNotificationEventStatus(NotificationEventStatus.ENQUEUED);
                event.setRetryAttempts(event.getRetryAttempts() + 1L);

                event.getCustomerData().replace("scheduledDate", OffsetDateTime.now().plusMinutes(retryAfterMinutes)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                event.setCustomerData(event.getCustomerData());

                log.info("Preserving notification event {} for retry in ENQUEUED status", event.getId());
                notificationEventRepository.save(event);
            });
        }
    }

    private List<RuleCacheResponse> getRules(NotificationEvent event) {
        Cache cache = cacheManager.getCache(FIND_RULE_CACHE);

        List<RuleCacheResponse> matchingRules = new ArrayList<>();
        if (Objects.nonNull(cache)) {
            matchingRules = cache.get(FIND_RULE_KEY, generifyClass());
        }

        if ((Objects.isNull(matchingRules) || matchingRules.isEmpty())) {
            matchingRules = new ArrayList<>();
            List<RuleCacheResponse> finalMatchingRules = matchingRules;
            this.ruleRepository.findAll().forEach(rule -> {
                        final var channel = rule.getChannel();
                        final var providers = channel.getProviders();
                        final var template = rule.getTemplate();

                        finalMatchingRules.add(getRuleCacheResponse(rule, channel, providers, template));
                    }
            );
            if (Objects.nonNull(cache)) {
                cache.put(FIND_RULE_KEY, matchingRules);
            }
        }
        return matchingRules.stream().filter(pre -> pre.eventType().equalsIgnoreCase(
                event.getEventType())).toList();
    }
}
