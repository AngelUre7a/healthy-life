package org.una.progra3.healthy_life.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProgressLogTest {
    @Test
    void testGettersAndSetters() {
        ProgressLog pl = new ProgressLog();
        pl.setId(1L);
        assertEquals(1L, pl.getId());
    }
}
