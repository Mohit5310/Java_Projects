package com.vroomz.user.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private final String secret = "vroomz_super_secret_key_for_shriram_transport_project_2026";
    private final Key key = Keys.hmacShaKeyFor(secret.getBytes());

 // JwtUtils.java (User-Service)
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role) // Token ke andar Role daal diya
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key)
                .compact();
    }
}