package com.cts.grantserve.budget_service.config;

import com.cts.grantserve.security.servlet.SharedSecurityUtils;
import com.cts.grantserve.security.servlet.SharedServletConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Import(SharedServletConfig.class)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        SharedSecurityUtils.applySharedSecurity(httpSecurity);
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/budgets/create").hasAuthority("SCOPE_MANAGER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/budgets/**").hasAuthority("SCOPE_MANAGER")
                        .anyRequest().authenticated() // LOCK EVERYTHING ELSE
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }

}