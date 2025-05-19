package com.logistics.LogisticsFuture.utility;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret:qyZHXv8fHtrAxoINKlp6W911ky6MmYGZ=30emt3bLaTAjs--TnrhWrSfSTCvLZTvcpT5eec=FD1u-mR!kV7xM3uhlsQq/OUjf=KYuOent90hJfmABV6s1iFmUU/g0pO-dYSwWMkB?oFFZMNA0tWplNs0A3l?Bb613PpNM8x5IDgipY=c0AoJZSkvJeS7-BLMP!8LEpTA?9Bfvfs/siXYHlFhDVtq7D4N7sgq3c8Q3gzvtmswUcSIIJeF2tz2TS/U}")
    private String secret;

    @Value("${jwt.expiration:3600000}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh.expiration:604800000}")
    private long refreshTokenExpiration;

    public String generateToken(UUID userId, String email) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(accessTokenExpiration);
        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public String generateRefreshToken(UUID userId) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(refreshTokenExpiration);
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject());
    }
    public String getTokenType(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("type", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
