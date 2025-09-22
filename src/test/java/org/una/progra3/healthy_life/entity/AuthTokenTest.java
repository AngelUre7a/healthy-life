package org.una.progra3.healthy_life.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

public class AuthTokenTest {
    @Test
    void testConstructorAndGetters() {
        Long id = 1L;
        String token = "abc123";
        LocalDateTime expiration = LocalDateTime.now().plusDays(1);
        AuthToken authToken = new AuthToken();
        authToken.setId(id);
        authToken.setToken(token);
           authToken.setExpiresAt(expiration);

        assertEquals(id, authToken.getId());
        assertEquals(token, authToken.getToken());
           assertEquals(expiration, authToken.getExpiresAt());
    }

    @Test
    void testSetters() {
        AuthToken authToken = new AuthToken();
        authToken.setId(2L);
        authToken.setToken("xyz789");
        LocalDateTime expiration = LocalDateTime.now().plusDays(2);
           authToken.setExpiresAt(expiration);

        assertEquals(2L, authToken.getId());
        assertEquals("xyz789", authToken.getToken());
           assertEquals(expiration, authToken.getExpiresAt());
    }

}
