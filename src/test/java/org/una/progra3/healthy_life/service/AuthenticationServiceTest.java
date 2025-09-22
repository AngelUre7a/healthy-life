package org.una.progra3.healthy_life.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.entity.Role;
import org.una.progra3.healthy_life.entity.enums.RoleType;
import org.una.progra3.healthy_life.security.graphql.responses.LoginResponse;
import org.una.progra3.healthy_life.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @Mock
    UserService userService;
    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    HttpServletRequest httpServletRequest;
    @InjectMocks
    AuthenticationService authenticationService;

    @Test
    void testLoginSuccess() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");
        Role role = new Role();
        role.setName(RoleType.USER);
        user.setRole(role);
        Mockito.when(userService.login("test@example.com", "password")).thenReturn(user);
        Mockito.when(jwtTokenProvider.generateAccessToken("test@example.com", "test@example.com", 1L, "USER")).thenReturn("token123");
        LoginResponse response = authenticationService.login("test@example.com", "password");
        assertNotNull(response);
        assertEquals("token123", response.getToken());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getName());
        assertEquals("USER", response.getRole());
    }

    @Test
    void testLoginInvalidCredentials() {
        Mockito.when(userService.login("bad@example.com", "wrong")).thenReturn(null);
        Exception ex = assertThrows(RuntimeException.class, () -> authenticationService.login("bad@example.com", "wrong"));
        assertEquals("Credenciales inválidas", ex.getMessage());
    }

    @Test
    void testLoginWithNullRole() {
        User user = new User();
        user.setId(1L);
        user.setEmail("no-role@example.com");
        user.setName("No Role");
        Mockito.when(userService.login("no-role@example.com", "pwd")).thenReturn(user);
        Mockito.when(jwtTokenProvider.generateAccessToken("no-role@example.com", "no-role@example.com", 1L, null)).thenReturn("tokenXYZ");
        LoginResponse response = authenticationService.login("no-role@example.com", "pwd");
        assertNotNull(response);
        assertEquals("tokenXYZ", response.getToken());
        assertNull(response.getRole());
    }

    @Test
    void testGetCurrentUserSuccess() {
        User user = new User();
        user.setId(1L);
        Mockito.when(jwtTokenProvider.resolveToken(httpServletRequest)).thenReturn(java.util.Optional.of("jwtToken"));
        Mockito.when(jwtTokenProvider.getUserIdFromToken("jwtToken")).thenReturn(1L);
        Mockito.when(userService.findById(1L)).thenReturn(user);
        ServletRequestAttributes attrs = Mockito.mock(ServletRequestAttributes.class);
        Mockito.when(attrs.getRequest()).thenReturn(httpServletRequest);
        RequestContextHolder.setRequestAttributes(attrs);
        User result = authenticationService.getCurrentUser();
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetCurrentUserNoRequestContext() {
        RequestContextHolder.resetRequestAttributes();
        Exception ex = assertThrows(RuntimeException.class, () -> authenticationService.getCurrentUser());
        assertEquals("No request context available", ex.getMessage());
    }

    @Test
    void testGetCurrentUserNoToken() {
        ServletRequestAttributes attrs = Mockito.mock(ServletRequestAttributes.class);
        Mockito.when(attrs.getRequest()).thenReturn(httpServletRequest);
        RequestContextHolder.setRequestAttributes(attrs);
        Mockito.when(jwtTokenProvider.resolveToken(httpServletRequest)).thenReturn(java.util.Optional.empty());
        Exception ex = assertThrows(RuntimeException.class, () -> authenticationService.getCurrentUser());
        assertEquals("No JWT token found", ex.getMessage());
    }

    @Test
    void testGetCurrentUserInvalidToken() {
        ServletRequestAttributes attrs = Mockito.mock(ServletRequestAttributes.class);
        Mockito.when(attrs.getRequest()).thenReturn(httpServletRequest);
        RequestContextHolder.setRequestAttributes(attrs);
        Mockito.when(jwtTokenProvider.resolveToken(httpServletRequest)).thenReturn(java.util.Optional.of("jwtToken"));
        Mockito.when(jwtTokenProvider.getUserIdFromToken("jwtToken")).thenReturn(null);
        Exception ex = assertThrows(RuntimeException.class, () -> authenticationService.getCurrentUser());
        assertEquals("Invalid JWT token: no user ID", ex.getMessage());
    }

    @Test
    void testGetCurrentUserUserNotFound() {
        ServletRequestAttributes attrs = Mockito.mock(ServletRequestAttributes.class);
        Mockito.when(attrs.getRequest()).thenReturn(httpServletRequest);
        RequestContextHolder.setRequestAttributes(attrs);
        Mockito.when(jwtTokenProvider.resolveToken(httpServletRequest)).thenReturn(java.util.Optional.of("jwtToken"));
        Mockito.when(jwtTokenProvider.getUserIdFromToken("jwtToken")).thenReturn(99L);
        Mockito.when(userService.findById(99L)).thenReturn(null);
        Exception ex = assertThrows(RuntimeException.class, () -> authenticationService.getCurrentUser());
        assertEquals("User not found", ex.getMessage());
    }
}
