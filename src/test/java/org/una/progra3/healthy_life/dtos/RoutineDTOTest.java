package org.una.progra3.healthy_life.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class RoutineDTOTest {
    @Test
    void testGettersAndSetters() {
        RoutineDTO dto = new RoutineDTO();
        Long id = 1L;
        String title = "Rutina";
        Long userId = 2L;
        String daysOfWeek = "Lunes,Miércoles,Viernes";
        List<Long> activityIds = List.of(3L, 4L);

        dto.setId(id);
        dto.setTitle(title);
        dto.setUserId(userId);
        dto.setDaysOfWeek(daysOfWeek);
        dto.setActivityIds(activityIds);

        assertEquals(id, dto.getId());
        assertEquals(title, dto.getTitle());
        assertEquals(userId, dto.getUserId());
        assertEquals(daysOfWeek, dto.getDaysOfWeek());
        assertEquals(activityIds, dto.getActivityIds());
    }
}
