package org.una.progra3.healthy_life.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RoutineTest {
    @Test
    void testGettersAndSetters() {
        Routine routine = new Routine();
        routine.setId(1L);
        assertEquals(1L, routine.getId());
    }
}
