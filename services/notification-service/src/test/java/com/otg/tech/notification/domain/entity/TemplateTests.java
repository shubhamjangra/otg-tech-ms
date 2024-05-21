package com.otg.tech.notification.domain.entity;

import com.otg.tech.notification.domain.response.cache.TemplateCacheResponse;
import com.otg.tech.notification.template.TemplateEngine;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TemplateTests {

    @Test
    void should_hydrate_a_template_with_event_data() throws Exception {
        Template template = Template.builder()
                .templateCode("T001")
                .language("en")
                .bodyTemplate(
                        ("A secret code only between us. Use OTP {{event.otpValue}} for Login to Merchant Banking App. Valid till 00:02:58. - Bank").trim()
                                .getBytes())
                .build();
        var event = NotificationEvent.builder()
                .customerData(Map.of("name", "User 1"))
                .data(Map.of("otpValue", "123456"))
                .build();
        String hydrated = TemplateCacheResponse.getTemplateCacheResponse(template).hydrateTemplate(
                event,
                TemplateEngine.mustacheEngine()
        );
        assertThat(hydrated).isEqualTo(
                "A secret code only between us. Use OTP 123456 for Login to Merchant Banking App. Valid till 00:02:58. - Bank");
    }
}
