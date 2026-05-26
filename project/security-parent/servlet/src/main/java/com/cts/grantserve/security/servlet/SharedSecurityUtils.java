package com.cts.grantserve.security.servlet;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public class SharedSecurityUtils {
    public static HttpSecurity applySharedSecurity(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll() // Every service will now allow Admin
            )
            .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
    }
}