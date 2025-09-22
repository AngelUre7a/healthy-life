package org.una.progra3.healthy_life.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HabitDTOTest {
    @Test
    void testGettersAndSetters() {
        HabitDTO dto = new HabitDTO();
        Long id = 1L;
        String name = "Ejercicio";
        String category = "Salud";
        String description = "Descripción";

        dto.setId(id);
        dto.setName(name);
        dto.setCategory(category);
        dto.setDescription(description);

        assertEquals(id, dto.getId());
        assertEquals(name, dto.getName());
        assertEquals(category, dto.getCategory());
        assertEquals(description, dto.getDescription());
    }
}
