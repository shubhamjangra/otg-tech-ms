package com.otg.tech.notification.template;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

public class MustacheBasedTemplateEngine implements TemplateEngine {

    @Override
    public String execute(String templateName, String template, Map<String, Object> data) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(template), templateName);
        StringWriter writer = new StringWriter();
        mustache.execute(writer, data).flush();
        return writer.toString();
    }
}
