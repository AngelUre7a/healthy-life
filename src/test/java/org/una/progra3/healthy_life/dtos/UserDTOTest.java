package org.una.progra3.healthy_life.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.List;

public class UserDTOTest {
    @Test
    void testGettersAndSetters() {
        UserDTO dto = new UserDTO();
        Long id = 1L;
        String name = "Usuario";
        String email = "usuario@email.com";
        Long roleId = 2L;
        Set<Long> favoriteHabitIds = Set.of(3L, 4L);
        List<Long> routineIds = List.of(5L, 6L);
        List<Long> progressLogIds = List.of(7L, 8L);
        List<Long> reminderIds = List.of(9L, 10L);
        List<Long> authTokenIds = List.of(11L, 12L);

        dto.setId(id);
        dto.setName(name);
        dto.setEmail(email);
        dto.setRoleId(roleId);
        dto.setFavoriteHabitIds(favoriteHabitIds);
        dto.setRoutineIds(routineIds);
        dto.setProgressLogIds(progressLogIds);
        dto.setReminderIds(reminderIds);
        dto.setAuthTokenIds(authTokenIds);

        assertEquals(id, dto.getId());
        assertEquals(name, dto.getName());
        assertEquals(email, dto.getEmail());
        assertEquals(roleId, dto.getRoleId());
        assertEquals(favoriteHabitIds, dto.getFavoriteHabitIds());
        assertEquals(routineIds, dto.getRoutineIds());
        assertEquals(progressLogIds, dto.getProgressLogIds());
        assertEquals(reminderIds, dto.getReminderIds());
        assertEquals(authTokenIds, dto.getAuthTokenIds());
    }
}
