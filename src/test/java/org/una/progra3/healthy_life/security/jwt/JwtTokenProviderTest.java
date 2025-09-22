package org.una.progra3.healthy_life.security.jwt;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider();
        // Inject configuration via reflection
        ReflectionTestUtils.setField(provider, "secretKeyString", "test-secret-key-123456");
        ReflectionTestUtils.setField(provider, "accessTokenValidity", 600_000L);
        ReflectionTestUtils.setField(provider, "refreshTokenValidity", 3_600_000L);
        ReflectionTestUtils.setField(provider, "cookieSecure", false);
        ReflectionTestUtils.setField(provider, "cookieSameSite", "Lax");
    }

    @Test
    void resolveToken_fromAuthorizationHeader() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer abc.def.ghi");
        Optional<String> tok = provider.resolveToken(req);
        assertTrue(tok.isPresent());
        assertEquals("abc.def.ghi", tok.get());
    }

    @Test
    void resolveToken_fromCookie() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCookies(new Cookie("access_token", "cookie.jwt.token"));
        Optional<String> tok = provider.resolveToken(req);
        assertTrue(tok.isPresent());
        assertEquals("cookie.jwt.token", tok.get());
    }

    @Test
    void buildAccessTokenCookie_respectsConfig() {
        // cookieSecure=false should omit Secure attribute
        ResponseCookie c1 = provider.buildAccessTokenCookie("tkn");
        String s1 = c1.toString();
        assertTrue(s1.contains("access_token=tkn"));
        assertTrue(s1.contains("HttpOnly"));
        assertTrue(s1.contains("Path=/"));
        assertTrue(s1.contains("SameSite=Lax"));
        assertFalse(s1.contains("Secure"));

        // Now switch to Secure + SameSite=None
        ReflectionTestUtils.setField(provider, "cookieSecure", true);
        ReflectionTestUtils.setField(provider, "cookieSameSite", "None");
        ResponseCookie c2 = provider.buildAccessTokenCookie("tkn2");
        String s2 = c2.toString();
        assertTrue(s2.contains("access_token=tkn2"));
        assertTrue(s2.contains("Secure"));
        assertTrue(s2.contains("SameSite=None"));
    }
}
