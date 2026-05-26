package com.cts.grantserve.security.servlet;

import com.cts.grantserve.security.core.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class SharedServletConfig {

    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.secret}") String secret) {
        // Using the same shared logic from Core!
        return NimbusJwtDecoder.withSecretKey(JwtUtils.getSigningKey(secret)).build();
    }


}