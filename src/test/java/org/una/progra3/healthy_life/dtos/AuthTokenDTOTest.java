package org.una.progra3.healthy_life.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthTokenDTOTest {
    @Test
    void testGettersAndSetters() {
        AuthTokenDTO dto = new AuthTokenDTO();
        Long id = 10L;
        String token = "abc123";
        java.time.LocalDateTime expiresAt = java.time.LocalDateTime.now().plusDays(1);
        Long userId = 99L;

        dto.setId(id);
        dto.setToken(token);
        dto.setExpiresAt(expiresAt);
        dto.setUserId(userId);

        assertEquals(id, dto.getId());
        assertEquals(token, dto.getToken());
        assertEquals(expiresAt, dto.getExpiresAt());
        assertEquals(userId, dto.getUserId());
    }
}
