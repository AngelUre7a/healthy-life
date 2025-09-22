package org.una.progra3.healthy_life.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HabitTest {
    @Test
    void testGettersAndSetters() {
        Habit habit = new Habit();
        habit.setId(1L);
        habit.setName("Ejercicio");
        assertEquals(1L, habit.getId());
        assertEquals("Ejercicio", habit.getName());
    }
}
