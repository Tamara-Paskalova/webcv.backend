package com.tamara.webcv.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;


@Configuration
@EnableWebSecurity
public class SecurityFilterConfig {

    @Autowired
    private RateLimitFilter rateLimitFilter;

    @Autowired
    private CaptchaFilter captchaFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .addFilterBefore(captchaFilter, AuthorizationFilter.class)
                .addFilterBefore(rateLimitFilter, AuthorizationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(("/*/admin/**")).permitAll()  //TODO change on deploy
                        .anyRequest().permitAll());
        return http.build();
    }
}
