package com.otg.tech.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.type.format.jackson.JacksonJsonFormatMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@EnableFeignClients
@ConfigurationPropertiesScan
@EnableJpaRepositories
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @Bean
    @SuppressWarnings("unused")
    public HibernatePropertiesCustomizer jsonFormatMapperCustomizer(ObjectMapper objectMapper) {
        return properties -> properties.put(AvailableSettings.JSON_FORMAT_MAPPER,
                new JacksonJsonFormatMapper(objectMapper));
    }

    @Bean
    @SuppressWarnings("unused")
    public TaskDecorator decorator() {
        return new ContextPropagatingTaskDecorator();
    }
}
