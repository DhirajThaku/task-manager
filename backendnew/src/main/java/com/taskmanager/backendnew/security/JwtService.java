package com.taskmanager.backendnew.security;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    // below are fetched from the application.properties
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    // encrypting the secret key
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());

    }

    // generating token
    public String generateToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder().subject(username).issuedAt(now).expiration(expiry).signWith(getSigningKey()).compact();
    }

    // get user name form token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> ClaimsResolver) {

        Claims claims = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
        return ClaimsResolver.apply(claims);
    }

    // validating token valid
    public boolean isTokenValid(String token, String username) {
        String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

}
