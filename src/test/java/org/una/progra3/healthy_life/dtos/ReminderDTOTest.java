package org.una.progra3.healthy_life.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

public class ReminderDTOTest {
    @Test
    void testGettersAndSetters() {
        ReminderDTO dto = new ReminderDTO();
        Long id = 1L;
        Long userId = 2L;
        Long habitId = 3L;
        LocalTime time = LocalTime.of(8, 30);
        String frequency = "Diario";

        dto.setId(id);
        dto.setUserId(userId);
        dto.setHabitId(habitId);
        dto.setTime(time);
        dto.setFrequency(frequency);

        assertEquals(id, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals(habitId, dto.getHabitId());
        assertEquals(time, dto.getTime());
        assertEquals(frequency, dto.getFrequency());
    }
}
