package org.una.progra3.healthy_life.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CompletedActivityTest {
    @Test
    void testGettersAndSetters() {
        CompletedActivity ca = new CompletedActivity();
        ca.setId(1L);
        assertEquals(1L, ca.getId());
    }
}
