package com.si516.saludconecta.util;

import com.si516.saludconecta.document.Doctor;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${app.jwt.secret:mySecretKey}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:30000000}") // Default: about 8 hours in milliseconds
    private long jwtExpiration;

    /**
     * Generates a JWT token for the given doctor
     */
    public String createToken(Doctor doctor) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpiration);
        
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.builder()
                .subject(doctor.getId())
                .claim("username", doctor.getUsername())
                .claim("fullName", doctor.getFullName())
                .claim("officeId", doctor.getOffice().getId())
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Validates the JWT token and returns the subject (doctor ID)
     */
    public String validateTokenAndGetSubject(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
                    
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            throw new RuntimeException("JWT token is expired");
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
            throw new RuntimeException("JWT token is unsupported");
        } catch (MalformedJwtException e) {
            log.error("JWT token is malformed: {}", e.getMessage());
            throw new RuntimeException("JWT token is malformed");
        } catch (IllegalArgumentException e) {
            log.error("JWT token compact or handler is invalid: {}", e.getMessage());
            throw new RuntimeException("JWT token compact or handler is invalid");
        }
    }

    /**
     * Check if the token is valid (not expired and properly signed)
     */
    public boolean isTokenValid(String token) {
        try {
            validateTokenAndGetSubject(token);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}