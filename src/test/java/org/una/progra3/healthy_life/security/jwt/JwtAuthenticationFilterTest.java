package org.una.progra3.healthy_life.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class JwtAuthenticationFilterTest {

    private JwtTokenProvider provider;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setup() {
        provider = mock(JwtTokenProvider.class);
        filter = new JwtAuthenticationFilter(provider);
    }

    @Test
    void noToken_header_shouldPassThroughWithoutAttributes() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(provider.resolveToken(request)).thenReturn(Optional.empty());

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).setAttribute(anyString(), any());
    }

    @Test
    void validToken_withAllClaims_setsAllAttributes() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(provider.resolveToken(request)).thenReturn(Optional.of("jwt"));
        when(provider.getUsernameFromToken("jwt")).thenReturn("alice");
        when(provider.isTokenExpired("jwt")).thenReturn(false);
        when(provider.getUserIdFromToken("jwt")).thenReturn(10L);
        when(provider.getEmailFromToken("jwt")).thenReturn("a@b.com");
        when(provider.getUserRoleFromToken("jwt")).thenReturn("ADMIN");

        filter.doFilterInternal(request, response, chain);

        verify(request).setAttribute("auth.username", "alice");
        verify(request).setAttribute("auth.userId", 10L);
        verify(request).setAttribute("auth.email", "a@b.com");
        verify(request).setAttribute("auth.role", "ADMIN");
        verify(chain).doFilter(request, response);
    }

    @Test
    void validToken_withPartialClaims_setsOnlyAvailable() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(provider.resolveToken(request)).thenReturn(Optional.of("jwt"));
        when(provider.getUsernameFromToken("jwt")).thenReturn("bob");
        when(provider.isTokenExpired("jwt")).thenReturn(false);
        when(provider.getUserIdFromToken("jwt")).thenReturn(null);
        when(provider.getEmailFromToken("jwt")).thenReturn(null);
        when(provider.getUserRoleFromToken("jwt")).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        verify(request).setAttribute("auth.username", "bob");
        verify(request, never()).setAttribute(eq("auth.userId"), any());
        verify(request, never()).setAttribute(eq("auth.email"), any());
        verify(request, never()).setAttribute(eq("auth.role"), any());
        verify(chain).doFilter(request, response);
    }

    @Test
    void tokenWithNullUsername_setsNoAttributes() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(provider.resolveToken(request)).thenReturn(Optional.of("jwt"));
        when(provider.getUsernameFromToken("jwt")).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        verify(request, never()).setAttribute(anyString(), any());
        verify(chain).doFilter(request, response);
    }

    @Test
    void expiredToken_setsNoAttributes() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(provider.resolveToken(request)).thenReturn(Optional.of("jwt"));
        when(provider.getUsernameFromToken("jwt")).thenReturn("carol");
        when(provider.isTokenExpired("jwt")).thenReturn(true);

        filter.doFilterInternal(request, response, chain);

        verify(request, never()).setAttribute(anyString(), any());
        verify(chain).doFilter(request, response);
    }

    @Test
    void providerThrows_exception_isSwallowed_andContinuesChain() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(provider.resolveToken(request)).thenThrow(new RuntimeException("boom"));

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).setAttribute(anyString(), any());
    }
}
