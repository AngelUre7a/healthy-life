package org.una.progra3.healthy_life.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GuideTest {
    @Test
    void testGettersAndSetters() {
        Guide guide = new Guide();
        guide.setId(1L);
        assertEquals(1L, guide.getId());
    }
}
