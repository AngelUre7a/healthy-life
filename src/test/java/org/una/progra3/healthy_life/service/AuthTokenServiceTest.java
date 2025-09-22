package org.una.progra3.healthy_life.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.una.progra3.healthy_life.entity.AuthToken;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.repository.AuthTokenRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthTokenServiceTest {
    @Mock
    AuthTokenRepository authTokenRepository;
    @InjectMocks
    AuthTokenService authTokenService;

    @Test
    void testFindAll() {
        AuthToken token = new AuthToken();
        Mockito.when(authTokenRepository.findAll()).thenReturn(List.of(token));
        List<AuthToken> result = authTokenService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void testFindById() {
        AuthToken token = new AuthToken();
        token.setId(1L);
        Mockito.when(authTokenRepository.findById(1L)).thenReturn(Optional.of(token));
        AuthToken result = authTokenService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindByToken() {
        AuthToken token = new AuthToken();
        token.setToken("abc123");
        Mockito.when(authTokenRepository.findByToken("abc123")).thenReturn(Optional.of(token));
        AuthToken result = authTokenService.findByToken("abc123");
        assertNotNull(result);
        assertEquals("abc123", result.getToken());
    }

    @Test
    void testFindByUser() {
        User user = new User();
        AuthToken token = new AuthToken();
        Mockito.when(authTokenRepository.findByUser(user)).thenReturn(List.of(token));
        List<AuthToken> result = authTokenService.findByUser(user);
        assertEquals(1, result.size());
    }

    @Test
    void testCreateValid() {
        User user = new User();
        String tokenStr = "abc123";
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);
        AuthToken token = new AuthToken();
        Mockito.when(authTokenRepository.save(Mockito.any())).thenReturn(token);
        AuthToken result = authTokenService.create(user, tokenStr, expiresAt);
        assertNotNull(result);
    }

    @Test
    void testCreateNullUser() {
        assertThrows(IllegalArgumentException.class, () -> authTokenService.create(null, "abc123", LocalDateTime.now()));
    }

    @Test
    void testCreateBlankToken() {
        User user = new User();
        assertThrows(IllegalArgumentException.class, () -> authTokenService.create(user, "", LocalDateTime.now()));
    }

    @Test
    void testCreateNullToken() {
        User user = new User();
        assertThrows(IllegalArgumentException.class, () -> authTokenService.create(user, null, LocalDateTime.now()));
    }

    @Test
    void testDeleteByIdExists() {
        Mockito.when(authTokenRepository.existsById(1L)).thenReturn(true);
        assertTrue(authTokenService.deleteById(1L));
    }

    @Test
    void testDeleteByIdNotExists() {
        Mockito.when(authTokenRepository.existsById(2L)).thenReturn(false);
        assertFalse(authTokenService.deleteById(2L));
    }
}
