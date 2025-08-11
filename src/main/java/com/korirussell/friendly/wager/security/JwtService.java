package com.korirussell.friendly.wager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final JwtProperties properties;
    private final SecretKey secretKey;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        byte[] keyBytes = Decoders.BASE64.decode(encodeBase64IfNeeded(properties.getSecret()));
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    private static String encodeBase64IfNeeded(String secret) {
        // If already base64, keep; else base64-encode
        try {
            Decoders.BASE64.decode(secret);
            return secret;
        } catch (RuntimeException ex) {
            // includes IllegalArgumentException and DecodingException
            return java.util.Base64.getEncoder().encodeToString(secret.getBytes());
        }
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds((long) properties.getExpirationMinutes() * 60);
        return Jwts.builder()
                .setIssuer(properties.getIssuer())
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .requireIssuer(properties.getIssuer())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

