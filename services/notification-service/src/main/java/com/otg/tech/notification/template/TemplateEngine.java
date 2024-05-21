package com.otg.tech.notification.template;

import java.io.IOException;
import java.util.Map;

public interface TemplateEngine {

    static TemplateEngine mustacheEngine() {
        return new MustacheBasedTemplateEngine();
    }

    String execute(String templateName, String template, Map<String, Object> data) throws IOException;
}
