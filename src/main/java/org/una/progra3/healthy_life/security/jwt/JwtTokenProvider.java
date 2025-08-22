package org.una.progra3.healthy_life.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.una.progra3.healthy_life.entity.enums.HabitCategory;
import org.una.progra3.healthy_life.entity.enums.ReminderFrequency;

import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);


    @Value("${jwt.secret}")
    private String secretKeyString;

    private String getSecretKey() {
        return secretKeyString;
    }

    @Value("${jwt.expiration.access}")
    private long accessTokenValidity;

    //Genera un token con claims personalizados para enums
    public String generateToken(String username, HabitCategory habitCategory, ReminderFrequency reminderFrequency) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenValidity);

    return Jwts.builder()
        .setSubject(username)
        .claim("habitCategory", habitCategory != null ? habitCategory.name() : null)
        .claim("reminderFrequency", reminderFrequency != null ? reminderFrequency.name() : null)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(SignatureAlgorithm.HS256, getSecretKey().getBytes())
        .compact();
    }

    // Método original para solo username
    public String generateToken(String username) {
        return generateToken(username, null, null);
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).map(Claims::getSubject).orElse(null);
    }

    public HabitCategory getHabitCategoryFromToken(String token) {
        return getClaimsFromToken(token)
                .map(claims -> {
                    String value = claims.get("habitCategory", String.class);
                    return value != null ? HabitCategory.valueOf(value) : null;
                })
                .orElse(null);
    }

    public ReminderFrequency getReminderFrequencyFromToken(String token) {
        return getClaimsFromToken(token)
                .map(claims -> {
                    String value = claims.get("reminderFrequency", String.class);
                    return value != null ? ReminderFrequency.valueOf(value) : null;
                })
                .orElse(null);
    }

    public boolean validateToken(String token, String username) {
        String tokenUsername = getUsernameFromToken(token);
        return username.equals(tokenUsername) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return getClaimsFromToken(token)
                .map(this::isTokenExpired)
                .orElse(true);
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    private Optional<Claims> getClaimsFromToken(String token) {
        try {
            return Optional.of(
                Jwts.parser()
                    .setSigningKey(getSecretKey().getBytes())
                    .parseClaimsJws(token)
                    .getBody()
            );
        } catch (ExpiredJwtException e) {
            logger.warn("El token ha expirado: {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error al parsear el token JWT: {}", e.getMessage());
            return Optional.empty();
        }
    }
}