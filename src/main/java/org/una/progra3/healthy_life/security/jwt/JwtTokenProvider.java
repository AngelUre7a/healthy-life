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
import javax.servlet.http.HttpServletRequest;

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

    @Value("${jwt.expiration.refresh}")
    private long refreshTokenValidity;



    // Genera un token flexible con claims de usuario y claims opcionales
    public String generateToken(String username, String email, Long userId, String role, boolean isRefreshToken) {
        long validityDuration = isRefreshToken ? refreshTokenValidity : accessTokenValidity;
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validityDuration);
        var builder = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, getSecretKey().getBytes());
        if (email != null) builder.claim("email", email);
        if (userId != null) builder.claim("id", userId);
        if (role != null) builder.claim("role", role);
        return builder.compact();
    }

    // Genera un access token con claims de usuario
    public String generateAccessToken(String username, String email, Long userId, String role) {
        return generateToken(username, email, userId, role, false);
    }

    // Genera un refresh token solo con username
    public String generateRefreshToken(String username) {
        return generateToken(username, null, null, null, true);
    }

    // Método para extraer el token del header Authorization
    public Optional<String> resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return (bearerToken != null && bearerToken.startsWith("Bearer "))
                ? Optional.of(bearerToken.substring(7))
                : Optional.empty();
    }


    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).map(Claims::getSubject).orElse(null);
    }

    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).map(claims -> claims.get("email", String.class)).orElse(null);
    }

    public Long getUserIdFromToken(String token) {
        return getClaimsFromToken(token).map(claims -> claims.get("id", Long.class)).orElse(null);
    }

    public String getUserRoleFromToken(String token) {
        return getClaimsFromToken(token).map(claims -> claims.get("role", String.class)).orElse(null);
    }

    // Métodos legacy para claims personalizados (opcional, puedes eliminar si no los usas)
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