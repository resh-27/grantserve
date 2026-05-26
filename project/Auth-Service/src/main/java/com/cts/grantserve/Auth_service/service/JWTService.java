package com.cts.grantserve.Auth_service.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Slf4j
@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String Secretkey;

    public String generateToken(String username, String role, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("scope", role);
        claims.put("userId", userId);
        return Jwts.builder()
                .header()
                .and()
                .claims(claims)
                .claim("type","ACCESS")
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 100))
                .signWith(getSignKey())
                .compact();
    }
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(Secretkey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}