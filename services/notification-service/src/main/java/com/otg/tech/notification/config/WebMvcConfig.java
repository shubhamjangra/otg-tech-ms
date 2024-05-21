package com.otg.tech.notification.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER;

@Configuration
@SuppressWarnings("unused")
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${rest.api.base-path}")
    private String restApiBasePath;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(
                restApiBasePath,
                HandlerTypePredicate.forBasePackage()
                        .and(HandlerTypePredicate.forAnnotation(RestController.class)));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*");
    }

    @Bean
    ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Notification Service")
                                .description("This service provides operation to send variety of notifications")
                                .version("v1"))
                .servers(List.of(new Server().url("/").description("Default Server URL")))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearer-key",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .in(HEADER)))
                .security(List.of(new SecurityRequirement().addList("bearer-key")));
    }
}
