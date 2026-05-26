package com.cognizant.researcher_service.config;

import com.cts.grantserve.security.servlet.SharedSecurityUtils;
import com.cts.grantserve.security.servlet.SharedServletConfig; // Use the SERVLET version
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Import(SharedServletConfig.class)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Note: If applySharedSecurity adds global filters,
        // make sure it doesn't conflict with your custom matchers below.
        SharedSecurityUtils.applySharedSecurity(http);

        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. Internal sync from Auth Service (No Token Needed)
                        .requestMatchers("/api/researcher/register").permitAll()

                        // 2. Public endpoints (if any)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // 3. Everything else (including /Update/**) requires JWT
                        .anyRequest().authenticated()
                )
                // This tells Spring to look for the "Authorization: Bearer <token>" header
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
                .build();
    }
}