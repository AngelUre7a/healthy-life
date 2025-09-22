package org.una.progra3.healthy_life.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

public class ProgressLogDTOTest {
    @Test
    void testGettersAndSetters() {
        ProgressLogDTO dto = new ProgressLogDTO();
        Long id = 1L;
        Long userId = 2L;
        Long routineId = 3L;
        LocalDate date = LocalDate.now();
        List<Long> completedActivityIds = List.of(4L, 5L);

        dto.setId(id);
        dto.setUserId(userId);
        dto.setRoutineId(routineId);
        dto.setDate(date);
        dto.setCompletedActivityIds(completedActivityIds);

        assertEquals(id, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals(routineId, dto.getRoutineId());
        assertEquals(date, dto.getDate());
        assertEquals(completedActivityIds, dto.getCompletedActivityIds());
    }
}
