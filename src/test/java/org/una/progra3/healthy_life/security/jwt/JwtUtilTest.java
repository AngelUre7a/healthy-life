package org.una.progra3.healthy_life.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class JwtUtilTest {

    @Test
    void getBearerToken_validHeader_returnsToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(JwtUtil.AUTH_HEADER)).thenReturn("Bearer abc.def.ghi");

        Optional<String> token = JwtUtil.getBearerToken(request);
        assertTrue(token.isPresent());
        assertEquals("abc.def.ghi", token.get());
    }

    @Test
    void getBearerToken_invalidOrMissingHeader_returnsEmpty() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(JwtUtil.AUTH_HEADER)).thenReturn("Token x.y.z");
        assertTrue(JwtUtil.getBearerToken(request).isEmpty());

        when(request.getHeader(JwtUtil.AUTH_HEADER)).thenReturn(null);
        assertTrue(JwtUtil.getBearerToken(request).isEmpty());

        when(request.getHeader(JwtUtil.AUTH_HEADER)).thenReturn("bearer abc.def"); // case-sensitive
        assertTrue(JwtUtil.getBearerToken(request).isEmpty());
    }

    @Test
    void setBearerToken_setsHeader_whenValid() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        JwtUtil.setBearerToken(response, "abc.def.ghi");
        verify(response).setHeader(JwtUtil.AUTH_HEADER, JwtUtil.BEARER_PREFIX + "abc.def.ghi");
    }

    @Test
    void setBearerToken_ignoresNullOrBlank() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        JwtUtil.setBearerToken(response, null);
        JwtUtil.setBearerToken(response, " ");
        verify(response, never()).setHeader(eq(JwtUtil.AUTH_HEADER), anyString());
    }

    @Test
    void maskTokenForLog_handlesNullShortAndLong() {
        assertEquals("null", JwtUtil.maskTokenForLog(null));

        String shortToken = "12345678901234567890"; // length 20
        assertEquals("***", JwtUtil.maskTokenForLog(shortToken));

        String longToken = "ABCDEFGHIJ" + "MIDPART" + "UVWXYZ"; // start 10, end 6 will be extracted
        String masked = JwtUtil.maskTokenForLog(longToken);
        assertTrue(masked.startsWith("ABCDEFGHIJ..."));
        assertTrue(masked.endsWith("WXYZ")); // last 6 of longToken are "UVWXYZ"
        assertTrue(masked.endsWith("UVWXYZ"));
    }

    @Test
    void attachAuthAttributes_ignoresNullOrBlankToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        JwtTokenProvider provider = mock(JwtTokenProvider.class);

        JwtUtil.attachAuthAttributes(request, provider, null);
        JwtUtil.attachAuthAttributes(request, provider, " ");

        verify(request, never()).setAttribute(anyString(), any());
    }

    @Test
    void attachAuthAttributes_ignoresWhenUsernameNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        JwtTokenProvider provider = mock(JwtTokenProvider.class);
        String token = "tkn";

        when(provider.getUsernameFromToken(token)).thenReturn(null);
        JwtUtil.attachAuthAttributes(request, provider, token);

        verify(request, never()).setAttribute(anyString(), any());
    }

    @Test
    void attachAuthAttributes_ignoresWhenExpired() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        JwtTokenProvider provider = mock(JwtTokenProvider.class);
        String token = "tkn";

        when(provider.getUsernameFromToken(token)).thenReturn("alice");
        when(provider.isTokenExpired(token)).thenReturn(true);
        JwtUtil.attachAuthAttributes(request, provider, token);

        verify(request, never()).setAttribute(anyString(), any());
    }

    @Test
    void attachAuthAttributes_setsAllPresentClaims() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        JwtTokenProvider provider = mock(JwtTokenProvider.class);
        String token = "tkn";

        when(provider.getUsernameFromToken(token)).thenReturn("alice");
        when(provider.isTokenExpired(token)).thenReturn(false);
        when(provider.getUserIdFromToken(token)).thenReturn(42L);
        when(provider.getEmailFromToken(token)).thenReturn("a@b.com");
        when(provider.getUserRoleFromToken(token)).thenReturn("ADMIN");

        JwtUtil.attachAuthAttributes(request, provider, token);

        verify(request).setAttribute(JwtUtil.ATTR_USERNAME, "alice");
        verify(request).setAttribute(JwtUtil.ATTR_USER_ID, 42L);
        verify(request).setAttribute(JwtUtil.ATTR_EMAIL, "a@b.com");
        verify(request).setAttribute(JwtUtil.ATTR_ROLE, "ADMIN");
    }

    @Test
    void attachAuthAttributes_setsOnlyPresentClaims() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        JwtTokenProvider provider = mock(JwtTokenProvider.class);
        String token = "tkn";

        when(provider.getUsernameFromToken(token)).thenReturn("bob");
        when(provider.isTokenExpired(token)).thenReturn(false);
        when(provider.getUserIdFromToken(token)).thenReturn(null);
        when(provider.getEmailFromToken(token)).thenReturn(null);
        when(provider.getUserRoleFromToken(token)).thenReturn(null);

        JwtUtil.attachAuthAttributes(request, provider, token);

        verify(request).setAttribute(JwtUtil.ATTR_USERNAME, "bob");
        verify(request, never()).setAttribute(eq(JwtUtil.ATTR_USER_ID), any());
        verify(request, never()).setAttribute(eq(JwtUtil.ATTR_EMAIL), any());
        verify(request, never()).setAttribute(eq(JwtUtil.ATTR_ROLE), any());
    }
}
