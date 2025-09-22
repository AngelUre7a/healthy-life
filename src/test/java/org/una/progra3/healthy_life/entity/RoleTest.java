package org.una.progra3.healthy_life.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RoleTest {
    @Test
    void testGettersAndSetters() {
        Role role = new Role();
        role.setId(1L);
        assertEquals(1L, role.getId());
    }
}
