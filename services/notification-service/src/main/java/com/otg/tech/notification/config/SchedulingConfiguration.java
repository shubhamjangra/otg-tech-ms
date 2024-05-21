package com.otg.tech.notification.config;

import com.otg.tech.notification.scheduler.NotificationPoller;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@ConditionalOnProperty(value = "notification.service.scheduling.enable", havingValue = "true", matchIfMissing = true)
@Configuration
@EnableScheduling
@EnableAsync
@Slf4j
@SuppressWarnings("unused")
@Observed
public class SchedulingConfiguration {

    @Autowired
    protected NotificationPoller notificationPoller;

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix("NotificationPoller-sendNotifications-");
        threadPoolTaskExecutor.setCorePoolSize(20);
        threadPoolTaskExecutor.setMaxPoolSize(20);
        threadPoolTaskExecutor.setQueueCapacity(5);
        return threadPoolTaskExecutor;
    }

    @Scheduled(fixedDelay = 2)
    void recurringNotification() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            try {
                taskExecutor().execute(() -> {
                    log.info("Running sendNotification in NotificationPoller.");
                    notificationPoller.sendNotifications();
                });
            } catch (Exception exception) {
                Thread.sleep(5000);
                log.info("Currently executing thread to sleep (temporarily cease execution) for 5000 milliseconds, "
                        + "subject to the precision and accuracy of system timers and schedulers.", exception);
            }
        }
    }
}
