package org.una.progra3.healthy_life.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.una.progra3.healthy_life.entity.enums.HabitCategory;
import org.una.progra3.healthy_life.entity.enums.ReminderFrequency;

import jakarta.servlet.http.Cookie;
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

    // Configuración de cookies (parametrizable desde application.properties)
    @Value("${jwt.cookie.secure:true}")
    private boolean cookieSecure;

    @Value("${jwt.cookie.samesite:Lax}")
    private String cookieSameSite;



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

    // Método para extraer el token del header Authorization o de la cookie "access_token"
    public Optional<String> resolveToken(jakarta.servlet.http.HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return Optional.of(bearerToken.substring(7));
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    return Optional.ofNullable(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    // Helpers para emitir/limpiar cookies de access/refresh token
    public ResponseCookie buildAccessTokenCookie(String token) {
        return ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(cookieSecure)   // En producción debe ser true (HTTPS)
                .path("/")
                .sameSite(cookieSameSite) // Usa "None" si el frontend está en otro dominio y envías credenciales cross-site
                .maxAge(accessTokenValidity / 1000) // maxAge en segundos
                .build();
    }

    public ResponseCookie clearAccessTokenCookie() {
    return ResponseCookie.from("access_token", "")
                .httpOnly(true)
        .secure(cookieSecure)
                .path("/")
        .sameSite(cookieSameSite)
                .maxAge(0)
                .build();
    }

    public ResponseCookie buildRefreshTokenCookie(String token) {
    return ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
        .secure(cookieSecure)
                .path("/auth/refresh") // limita el scope si quieres
        .sameSite(cookieSameSite)
                .maxAge(refreshTokenValidity / 1000)
                .build();
    }

    public ResponseCookie clearRefreshTokenCookie() {
    return ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
        .secure(cookieSecure)
                .path("/auth/refresh")
        .sameSite(cookieSameSite)
                .maxAge(0)
                .build();
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