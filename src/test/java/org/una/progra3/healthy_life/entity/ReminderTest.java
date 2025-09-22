package org.una.progra3.healthy_life.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReminderTest {
    @Test
    void testGettersAndSetters() {
        Reminder reminder = new Reminder();
        reminder.setId(1L);
        assertEquals(1L, reminder.getId());
    }
}
