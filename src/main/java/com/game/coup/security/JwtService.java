package com.game.coup.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_STRING = "your-super-secret-32-byte-long-key-for-coup-game-security";
    private final SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));
    
    // 1 Day TTL in milliseconds (24 hours * 60 mins * 60 secs * 1000 ms)
    private static final long JWT_TTL_MS = 24 * 60 * 60 * 1000L;

    public String generateToken(String playerName, String roomId) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + JWT_TTL_MS); // Calculate the exact cutoff point

        return Jwts.builder()
                .subject(playerName)
                .claim("roomId", roomId)
                .issuedAt(now)
                .expiration(expirationDate) // ADDED: Hard expiration mathematical claim
                .signWith(secretKey)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}