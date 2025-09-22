package org.una.progra3.healthy_life.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.una.progra3.healthy_life.entity.enums.HabitCategory;
import org.una.progra3.healthy_life.entity.enums.ReminderFrequency;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenProviderTest {

    private JwtTokenProvider provider;
    private final String secret = "test-secret-123";

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "secretKeyString", secret);
        ReflectionTestUtils.setField(provider, "accessTokenValidity", 60_000L); // 1 min
        ReflectionTestUtils.setField(provider, "refreshTokenValidity", 120_000L); // 2 min
    }

    @Test
    void generateAccessToken_and_parseClaims() {
        String token = provider.generateAccessToken("alice", "a@b.com", 42L, "ADMIN");
        assertEquals("alice", provider.getUsernameFromToken(token));
        assertEquals("a@b.com", provider.getEmailFromToken(token));
        assertEquals(42L, provider.getUserIdFromToken(token));
        assertEquals("ADMIN", provider.getUserRoleFromToken(token));
        assertFalse(provider.isTokenExpired(token));
        assertTrue(provider.validateToken(token, "alice"));
        assertFalse(provider.validateToken(token, "mallory"));
    }

    @Test
    void generateRefreshToken_minimalClaims() {
        String token = provider.generateRefreshToken("bob");
        assertEquals("bob", provider.getUsernameFromToken(token));
        assertNull(provider.getEmailFromToken(token));
        assertNull(provider.getUserIdFromToken(token));
        assertNull(provider.getUserRoleFromToken(token));
        assertTrue(provider.validateToken(token, "bob"));
    }

    @Test
    void expiredToken_isDetected_andNotValidated() {
        String expired = Jwts.builder()
                .setSubject("carol")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10_000))
                .setExpiration(new Date(System.currentTimeMillis() - 5_000))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
        assertNull(provider.getUsernameFromToken(expired));
        assertTrue(provider.isTokenExpired(expired));
        assertFalse(provider.validateToken(expired, "carol"));
    }

    @Test
    void invalidToken_returnsNullClaims_andInvalid() {
        String invalid = "not-a-jwt-token";
        assertNull(provider.getUsernameFromToken(invalid));
        assertNull(provider.getEmailFromToken(invalid));
        assertNull(provider.getUserIdFromToken(invalid));
        assertNull(provider.getUserRoleFromToken(invalid));
        assertTrue(provider.isTokenExpired(invalid));
        assertFalse(provider.validateToken(invalid, "dave"));
    }

    @Test
    void signatureMismatch_causesParseFailure() {
        String otherSecret = "another-secret";
        String token = Jwts.builder()
                .setSubject("erin")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(SignatureAlgorithm.HS256, otherSecret.getBytes())
                .compact();
        assertNull(provider.getUsernameFromToken(token));
        assertTrue(provider.isTokenExpired(token));
        assertFalse(provider.validateToken(token, "erin"));
    }

    @Test
    void resolveToken_fromAuthorizationHeader() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer abc.def.ghi");
        Optional<String> token = provider.resolveToken(request);
        assertTrue(token.isPresent());
        assertEquals("abc.def.ghi", token.get());
    }

    @Test
    void resolveToken_missingOrInvalidPrefix_returnsEmpty() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("Authorization")).thenReturn("Token x.y.z");
        assertTrue(provider.resolveToken(request).isEmpty());
        Mockito.when(request.getHeader("Authorization")).thenReturn(null);
        assertTrue(provider.resolveToken(request).isEmpty());
    }

    @Test
    void legacyClaims_parsedWhenPresent() {
        String token = Jwts.builder()
                .setSubject("frank")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .claim("habitCategory", HabitCategory.DIET.name())
                .claim("reminderFrequency", ReminderFrequency.DAILY.name())
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();

        assertEquals(HabitCategory.DIET, provider.getHabitCategoryFromToken(token));
        assertEquals(ReminderFrequency.DAILY, provider.getReminderFrequencyFromToken(token));
    }

    @Test
    void legacyClaims_missing_returnNull() {
        String token = Jwts.builder()
                .setSubject("ian")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();

        assertNull(provider.getHabitCategoryFromToken(token));
        assertNull(provider.getReminderFrequencyFromToken(token));
    }

    @Test
    void legacyClaims_invalidEnum_throwsException() {
        String token = Jwts.builder()
                .setSubject("jane")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .claim("habitCategory", "NOT_A_CATEGORY")
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();

        assertThrows(IllegalArgumentException.class, () -> provider.getHabitCategoryFromToken(token));
    }

    @Test
    void validateToken_withNullUsername_throwsNPE() {
        String token = provider.generateAccessToken("nick", null, null, null);
        assertThrows(NullPointerException.class, () -> provider.validateToken(token, null));
    }

    @Test
    void tokenWithoutSubject_returnsNullUsername_andValidateFalse() {
        String token = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .claim("email", "x@y.com")
                .claim("id", 99L)
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();

        assertNull(provider.getUsernameFromToken(token));
        assertFalse(provider.validateToken(token, "any"));
        assertFalse(provider.isTokenExpired(token));
    }

    @Test
    void idClaimStoredAsInteger_isExtractedSuccessfully() {
        String token = Jwts.builder()
                .setSubject("george")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                // store as Integer instead of Long
                .claim("id", 7)
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();

        assertEquals(7L, provider.getUserIdFromToken(token));
        assertEquals("george", provider.getUsernameFromToken(token));
    }

    @Test
    void nullToken_inputsHandledGracefully() {
        assertNull(provider.getUsernameFromToken(null));
        assertNull(provider.getEmailFromToken(null));
        assertNull(provider.getUserIdFromToken(null));
        assertNull(provider.getUserRoleFromToken(null));
        assertTrue(provider.isTokenExpired(null));
        assertFalse(provider.validateToken(null, "user"));
    }

    @Test
    void resolveToken_lowercaseBearer_prefixIsIgnored() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("Authorization")).thenReturn("bearer abc.def.ghi");
        assertTrue(provider.resolveToken(request).isEmpty());
    }

    @Test
    void generateAccessToken_withNullOptionalClaims_excludesThem() {
        String token = provider.generateAccessToken("henry", null, null, null);
        assertEquals("henry", provider.getUsernameFromToken(token));
        assertNull(provider.getEmailFromToken(token));
        assertNull(provider.getUserIdFromToken(token));
        assertNull(provider.getUserRoleFromToken(token));
        assertTrue(provider.validateToken(token, "henry"));
    }
}
