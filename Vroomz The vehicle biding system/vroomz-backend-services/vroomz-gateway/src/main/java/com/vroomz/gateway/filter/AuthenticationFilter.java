package com.vroomz.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.security.Key;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Value("${jwt.secret}")
    private String secret;
    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public AuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {
        // अगर फ़िल्टर में कोई कस्टमाइज़्ड कॉन्फ़िगरेशन चाहिए तो यहाँ वेरिएबल रख सकते हैं
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 1. Spring के getFirst() का उपयोग करें जो सुरक्षित रूप से पहली वैल्यू निकालता है
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            
            // अगर हेडर नहीं है या वह Bearer से शुरू नहीं होता, तो यहीं से ब्लॉक कर दो
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // 'Bearer ' हटाकर सिर्फ असली टोकन निकालें
            String token = authHeader.substring(7);

            try {
                // 2. Token Parse करके Claims (डेटा) निकालें
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String role = claims.get("role", String.class);
                String path = exchange.getRequest().getURI().getPath();

                // 3. ROLE BASED CHECK (केवल ADMIN ही वाहन जोड़ सकता है)
                if (path.contains("/vehicles/add") && !"ROLE_ADMIN".equals(role)) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN); // 403 Forbidden
                    return exchange.getResponse().setComplete();
                }

                // टोकन भी सही है और रोल भी, अब रिक्वेस्ट को आगे जाने दें
                return chain.filter(exchange);

            } catch (Exception e) {
                // टोकन एक्सपायर होने या गलत होने पर 401 Unauthorized भेजें
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }
}