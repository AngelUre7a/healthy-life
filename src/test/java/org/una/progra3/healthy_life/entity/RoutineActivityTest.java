package org.una.progra3.healthy_life.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RoutineActivityTest {
    @Test
    void testGettersAndSetters() {
        RoutineActivity ra = new RoutineActivity();
        ra.setId(1L);
        assertEquals(1L, ra.getId());
    }
}
