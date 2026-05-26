package com.cts.grantserve.security.reactive;

import com.cts.grantserve.security.core.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

@Configuration
public class SharedReactiveConfig {

    @Bean
    public ReactiveJwtDecoder jwtDecoder( @org.springframework.beans.factory.annotation.Value("${jwt.secret}") String secret) {
        return NimbusReactiveJwtDecoder.withSecretKey(JwtUtils.getSigningKey(secret)).build();
    }
}