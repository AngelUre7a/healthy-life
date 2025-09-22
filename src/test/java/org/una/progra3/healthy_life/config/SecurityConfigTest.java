package org.una.progra3.healthy_life.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    @Test
    void passwordEncoderBean_isBCrypt() {
        SecurityConfig cfg = new SecurityConfig();
        PasswordEncoder enc = cfg.passwordEncoder();
        assertNotNull(enc);
        String hash = enc.encode("secret");
        assertTrue(enc.matches("secret", hash));
    }
}
