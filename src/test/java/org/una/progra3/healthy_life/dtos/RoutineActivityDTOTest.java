package org.una.progra3.healthy_life.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

public class RoutineActivityDTOTest {
    @Test
    void testGettersAndSetters() {
        RoutineActivityDTO dto = new RoutineActivityDTO();
        Long id = 1L;
        Long routineId = 2L;
        Long habitId = 3L;
        Integer duration = 45;
        LocalTime targetTime = LocalTime.of(9, 0);
        String notes = "Notas de prueba";

        dto.setId(id);
        dto.setRoutineId(routineId);
        dto.setHabitId(habitId);
        dto.setDuration(duration);
        dto.setTargetTime(targetTime);
        dto.setNotes(notes);

        assertEquals(id, dto.getId());
        assertEquals(routineId, dto.getRoutineId());
        assertEquals(habitId, dto.getHabitId());
        assertEquals(duration, dto.getDuration());
        assertEquals(targetTime, dto.getTargetTime());
        assertEquals(notes, dto.getNotes());
    }
}
