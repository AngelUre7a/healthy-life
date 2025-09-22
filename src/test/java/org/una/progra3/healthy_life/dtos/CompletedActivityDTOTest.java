package org.una.progra3.healthy_life.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CompletedActivityDTOTest {
    @Test
    void testGettersAndSetters() {
        CompletedActivityDTO dto = new CompletedActivityDTO();
        Long id = 1L;
        Long habitId = 2L;
        Long progressLogId = 3L;
        java.time.LocalDateTime completedAt = java.time.LocalDateTime.now();
        String notes = "Test notes";

        dto.setId(id);
        dto.setHabitId(habitId);
        dto.setProgressLogId(progressLogId);
        dto.setCompletedAt(completedAt);
        dto.setNotes(notes);

        assertEquals(id, dto.getId());
        assertEquals(habitId, dto.getHabitId());
        assertEquals(progressLogId, dto.getProgressLogId());
        assertEquals(completedAt, dto.getCompletedAt());
        assertEquals(notes, dto.getNotes());
    }
}
