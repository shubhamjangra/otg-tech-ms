package com.otg.tech.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@SuppressWarnings("unused")
public class SecurityConfiguration {

    @Autowired
    protected AuthenticationProvider authenticationProvider;

    @Bean
    protected SecurityFilterChain configure(final HttpSecurity http,
                                            final AuthenticationManagerBuilder authBuilder,
                                            final AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
        http.csrf(AbstractHttpConfigurer::disable).cors(AbstractHttpConfigurer::disable).authorizeHttpRequests(
                        auth -> auth.requestMatchers(new AntPathRequestMatcher("/**"))
                                .permitAll().anyRequest().authenticated())
                .authenticationProvider(authenticationProvider)
                .addFilterAt(new AuthenticationFilter(authenticationManager), BasicAuthenticationFilter.class);

        return http.build();
    }
}
